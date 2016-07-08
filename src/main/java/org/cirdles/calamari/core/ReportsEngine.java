/*
 * Copyright 2006-2016 CIRDLES.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cirdles.calamari.core;

import org.cirdles.calamari.shrimp.IsotopeRatioModelSHRIMP;
import org.cirdles.calamari.shrimp.RawRatioNamesSHRIMP;
import org.cirdles.calamari.shrimp.ShrimpFraction;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.util.Arrays.asList;

/**
 *
 * @author James F. Bowring &lt;bowring at gmail.com&gt;
 */
public class ReportsEngine {

    private static File totalIonCountsAtMassFile;
    private static File totalSBMCountsAtMassFile;
    private static File totalCountsAtTimeStampAndTrimMass;
    private static File totalCountsPerSecondPerSpeciesPerAnalysis;
    private static File withinSpotRatiosAtInterpolatedTimes;
    private static File meanRatioAndSigmaPctPerIsotopicRatioPerAnalysis;

    private static File folderToWriteCalamariReports = new File(System.getProperty("user.dir"));
    private static transient String folderToWriteCalamariReportsPath;

    private static StringBuilder refMatFractionsTotalCountsPerSecondPerSpeciesPerAnalysis;
    private static StringBuilder unknownFractionsTotalCountsPerSecondPerSpeciesPerAnalysis;
    private static StringBuilder refMatWithinSpotRatiosAtInterpolatedTimes;
    private static StringBuilder unknownWithinSpotRatiosAtInterpolatedTimes;
    private static StringBuilder refMatMeanRatioAndSigmaPctPerIsotopicRatioPerAnalysis;
    private static StringBuilder unknownMeanRatioAndSigmaPctPerIsotopicRatioPerAnalysis;

    /**
     * ReportsEngine to test results
     *
     * @param shrimpFractions the value of shrimpFractions
     * @throws java.io.IOException
     */
    protected static void produceReports(List<ShrimpFraction> shrimpFractions) throws IOException {

        if (shrimpFractions.size() > 0) {
            // gather general info for all runs  from first fraction
            ShrimpFraction firstShrimpFraction = shrimpFractions.get(0);

            folderToWriteCalamariReportsPath
                    = folderToWriteCalamariReports.getCanonicalPath()
                    + File.separator + "CalamariReports-" 
                    + firstShrimpFraction.getNameOfMount()
                    + "--" + (firstShrimpFraction.isUseSBM() ? "SBM-TRUE" : "SBM-FALSE")
                    + "--" + (firstShrimpFraction.isUserLinFits() ? "FIT-TRUE" : "FIT-FALSE")
                    + File.separator;
            File reportsFolder = new File(folderToWriteCalamariReportsPath);
            reportsFolder.mkdir();

            prepSpeciesReportFiles(firstShrimpFraction);
            prepRatiosReportFiles(firstShrimpFraction);

            for (int f = 0; f < shrimpFractions.size(); f++) {
                ShrimpFraction shrimpFraction = shrimpFractions.get(f);
                shrimpFraction.setSpotNumber(f + 1);
                reportTotalIonCountsAtMass(shrimpFraction);
                reportTotalSBMCountsAtMass(shrimpFraction);
                reportTotalCountsAtTimeStampAndTrimMass(shrimpFraction);
                reportTotalCountsPerSecondPerSpeciesPerAnalysis(shrimpFraction);
                reportWithinSpotRatiosAtInterpolatedTimes(shrimpFraction);
                reportIsotopeRatiosMeanAndSigma(shrimpFraction);

            } // end of fractions loop

            finishSpeciesReportFiles();
            finishRatiosReportFiles();
        }
    }

    /**
     * 2016.May.3 email from Simon Bodorkos to Jim Bowring Step “0a” – Total ion
     * counts at mass We’ve touched on this one once before, informally. It is a
     * direct extract from the XML, with one row per scan, and one column per
     * ‘integration-value’. For the demo XML, the array will have 684 rows of
     * data (114 analyses x 6 scans), and 115 columns (5 for row identifiers,
     * then for each of the 10 measured species, 11 columns comprising
     * count_time_sec and the integer values of the 10 integrations).
     *
     * It needs five ‘left-hand’ columns to allow the rows to be identified and
     * sorted: Title = analysis-specific text-string read from XML Date =
     * analysis-specific date read from XML, to be expressed as YYYY-MM-DD
     * HH24:MI:SS Scan = integer, starting at 1 within each analysis Type =
     * “standard” or “unknown”; analyses with prefix “T.” to be labelled
     * “standard”, all others “unknown” Dead_time_ns = analysis-specific integer
     * read from XML
     *
     * These are to be followed by 11 columns for each species (i.e. 110 columns
     * for the demo XML): [entry-label].count_time_sec = analysis-specific
     * integer read from XML [entry-label].1 = integer value corresponding to
     * the first of 10 ‘integrations’ within tags “<data name = [entry-label]>
     * </data>” for the specified combination of analysis, scan and species
     * [entry-label].2 = integer value corresponding to the second of 10
     * ‘integrations’ within tags “<data name = [entry-label]> </data>” for the
     * specified combination of analysis, scan and species … [entry-label].10 =
     * integer value corresponding to the tenth of 10 ‘integrations’ within tags
     * “<data name = [entry-label]> </data>” for the specified combination of
     * analysis, scan and species
     *
     * Sorting: Primary criterion = Date, secondary criterion = Scan
     *
     * @param shrimpFraction
     * @param countOfSpecies
     */
    private static void reportTotalIonCountsAtMass(ShrimpFraction shrimpFraction) throws IOException {

        int countOfSpecies = shrimpFraction.getNamesOfSpecies().length;

        for (int scanNum = 0; scanNum < shrimpFraction.getRawPeakData().length; scanNum++) {
            StringBuilder dataLine = new StringBuilder();
            dataLine.append(shrimpFraction.getFractionID()).append(", ");
            dataLine.append(getFormattedDate(shrimpFraction.getDateTimeMilliseconds())).append(", ");
            dataLine.append(String.valueOf(scanNum + 1)).append(", ");
            dataLine.append(shrimpFraction.isReferenceMaterial() ? "ref mat" : "unknown").append(", ");
            dataLine.append(String.valueOf(shrimpFraction.getDeadTimeNanoseconds()));

            for (int i = 0; i < shrimpFraction.getRawPeakData()[scanNum].length; i++) {
                if ((i % countOfSpecies) == 0) {
                    dataLine.append(", ").append(String.valueOf(shrimpFraction.getCountTimeSec()[i / countOfSpecies]));
                }
                dataLine.append(", ").append(shrimpFraction.getRawPeakData()[scanNum][i]);
            }

            Files.write(totalIonCountsAtMassFile.toPath(), asList(dataLine), APPEND);
        }
    }

    /**
     * 2016.May.3 email from Simon Bodorkos to Jim Bowring Step “0b” – Total SBM
     * counts at mass As for step “0a” in all respects , except that in the
     * fifth ‘left-hand’ column, dead_time_ns should be discarded and replaced
     * by SBM_zero_cps = analysis-specific integer read from XML
     *
     * And the 11 columns for each species are: [entry-label].count_time_sec =
     * analysis-specific integer read from XML [entry-label].SBM.1 = integer
     * value corresponding to the first of 10 ‘integrations’ within tags “<data name = SBM
     * > </data>” for the specified combination of analysis, scan and species
     * [entry-label].SBM.2 = integer value corresponding to the second of 10
     * ‘integrations’ within tags “<data name = SBM > </data>” for the specified
     * combination of analysis, scan and species … [entry-label].SBM.10 =
     * integer value corresponding to the tenth of 10 ‘integrations’ within tags
     * “<data name = SBM> </data>” for the specified combination of analysis,
     * scan and species
     *
     * Sorting: Primary criterion = Date (ascending), secondary criterion = Scan
     * (ascending)
     *
     * @param shrimpFraction
     * @param countOfSpecies
     */
    private static void reportTotalSBMCountsAtMass(ShrimpFraction shrimpFraction) throws IOException {

        int countOfSpecies = shrimpFraction.getNamesOfSpecies().length;

        for (int scanNum = 0; scanNum < shrimpFraction.getRawSBMData().length; scanNum++) {
            StringBuilder dataLine = new StringBuilder();
            dataLine.append(shrimpFraction.getFractionID()).append(", ");
            dataLine.append(getFormattedDate(shrimpFraction.getDateTimeMilliseconds())).append(", ");
            dataLine.append(String.valueOf(scanNum + 1)).append(", ");
            dataLine.append(shrimpFraction.isReferenceMaterial() ? "ref mat" : "unknown").append(", ");
            dataLine.append(String.valueOf(shrimpFraction.getSbmZeroCps()));

            for (int i = 0; i < shrimpFraction.getRawSBMData()[scanNum].length; i++) {
                if ((i % countOfSpecies) == 0) {
                    dataLine.append(", ").append(String.valueOf(shrimpFraction.getCountTimeSec()[i / countOfSpecies]));
                }
                dataLine.append(", ").append(shrimpFraction.getRawSBMData()[scanNum][i]);
            }

            Files.write(totalSBMCountsAtMassFile.toPath(), asList(dataLine), APPEND);
        }
    }

    /**
     * 2016.May.3 email from Simon Bodorkos to Jim Bowring Step 1 – Total counts
     * at time-stamp and trim-mass This is intended to replicate the current
     * Step 1 sanity-check, with one row per scan, and one column per key
     * attribute of “total counts at peak”. For the demo XML, the array will
     * have 684 rows of data (114 analyses x 6 scans), and 54 columns (4 for row
     * identifiers, then for each of the 10 measured species, 5 columns as
     * specified below).
     *
     * It needs four ‘left-hand’ columns to allow the rows to be identified and
     * sorted: Title = analysis-specific text-string read from XML Date =
     * analysis-specific date read from XML, to be expressed as YYYY-MM-DD
     * HH24:MI:SS Scan = integer, starting at 1 within each analysis Type =
     * “standard” or “unknown”; analyses with prefix “T.” to be labelled
     * “standard”, all others “unknown”
     *
     * These are to be followed by 5 columns for each species (i.e. 50 columns
     * for the demo XML): [entry-label].Time = integer “time_stamp_sec” read
     * from XML for the specified combination of analysis, scan and species
     * [entry-label].TotalCounts = calculated decimal value for “total counts at
     * mass” from Step 1, for the specified combination of analysis, scan and
     * species [entry-label].1SigmaAbs = calculated decimal value for “+/-1sigma
     * at mass” from Step 1, for the specified combination of analysis, scan and
     * species [entry-label].TotalSBM = calculated decimal value for “total SBM
     * counts” from Step 1, for the specified combination of analysis, scan and
     * species [entry-label].TrimMass = decimal “trim_mass_amu” read from XML
     * for the specified combination of analysis, scan and species
     *
     * Sorting: Primary criterion = Date (ascending), secondary criterion = Scan
     * (ascending)
     *
     * @param shrimpFraction
     */
    private static void reportTotalCountsAtTimeStampAndTrimMass(ShrimpFraction shrimpFraction) throws IOException {

        for (int scanNum = 0; scanNum < shrimpFraction.getTimeStampSec().length; scanNum++) {
            StringBuilder dataLine = new StringBuilder();
            dataLine.append(shrimpFraction.getFractionID()).append(", ");
            dataLine.append(getFormattedDate(shrimpFraction.getDateTimeMilliseconds())).append(", ");
            dataLine.append(String.valueOf(scanNum + 1)).append(", ");
            dataLine.append(shrimpFraction.isReferenceMaterial() ? "ref mat" : "unknown");

            for (int i = 0; i < shrimpFraction.getTimeStampSec()[scanNum].length; i++) {
                dataLine.append(", ").append(shrimpFraction.getTimeStampSec()[scanNum][i]);
                dataLine.append(", ").append(shrimpFraction.getTotalCounts()[scanNum][i]);
                dataLine.append(", ").append(shrimpFraction.getTotalCountsOneSigmaAbs()[scanNum][i]);
                dataLine.append(", ").append(shrimpFraction.getTotalCountsSBM()[scanNum][i]);
                dataLine.append(", ").append(shrimpFraction.getTrimMass()[scanNum][i]);

                // these lines produce the big decimal scale 20 numbers used to check floating point math
//                dataLine.append(", ").append(shrimpFraction.getTimeStampSec()[scanNum][i]);
//                dataLine.append(", ").append(shrimpFraction.getTotalCountsBD()[scanNum][i].setScale(20, RoundingMode.HALF_EVEN).toPlainString());
//                dataLine.append(", ").append(shrimpFraction.getTotalCountsOneSigmaAbsBD()[scanNum][i].setScale(20, RoundingMode.HALF_EVEN).toPlainString());
//                dataLine.append(", ").append(shrimpFraction.getTotalCountsSBMBD()[scanNum][i].setScale(20, RoundingMode.HALF_EVEN).toPlainString());
//                dataLine.append(", ").append(shrimpFraction.getTrimMass()[scanNum][i]);
            }

            Files.write(totalCountsAtTimeStampAndTrimMass.toPath(), asList(dataLine), APPEND);
        }
    }

    /**
     * 2016.May.3 email from Simon Bodorkos to Jim Bowring Step 2 – Total
     * counts-per-second, per species, per analysis This is intended to
     * replicate the current Step 2 sanity-check, with one row per *analysis*,
     * and one column per species. For the demo XML, the array will have 114
     * rows of data (one per analysis), and 13 columns (3 for row identifiers,
     * then one for each of the 10 measured species).
     *
     * It needs three ‘left-hand’ columns to allow the rows to be identified and
     * sorted: Title = analysis-specific text-string read from XML Date =
     * analysis-specific date read from XML, to be expressed as YYYY-MM-DD
     * HH24:MI:SS Type = “standard” or “unknown”; analyses with prefix “T.” to
     * be labelled “standard”, all others “unknown”
     *
     * These are to be followed by 1 column for each species (i.e. 10 columns
     * for the demo XML): [entry-label].TotalCps = calculated decimal value for
     * “total counts per second” from Step 2, for the specified combination of
     * analysis and species
     *
     * Sorting: Primary criterion = Type (ascending; “standard” before unknown,
     * so alphabetical would do), secondary criterion = Date (ascending)
     *
     * @param shrimpFraction the value of shrimpFraction
     */
    private static void reportTotalCountsPerSecondPerSpeciesPerAnalysis(ShrimpFraction shrimpFraction) {

        // need to sort by reference material vs unknown
        StringBuilder dataLine = new StringBuilder();
        dataLine.append(shrimpFraction.getFractionID()).append(", ");
        dataLine.append(getFormattedDate(shrimpFraction.getDateTimeMilliseconds())).append(", ");
        dataLine.append(shrimpFraction.isReferenceMaterial() ? "ref mat" : "unknown");

        for (int i = 0; i < shrimpFraction.getTotalCps().length; i++) {
            dataLine.append(", ").append(shrimpFraction.getTotalCps()[i]);
        }

        dataLine.append("\n");
        if (shrimpFraction.isReferenceMaterial()) {
            refMatFractionsTotalCountsPerSecondPerSpeciesPerAnalysis.append(dataLine);
        } else {
            unknownFractionsTotalCountsPerSecondPerSpeciesPerAnalysis.append(dataLine);
        }

    }

    private static void reportWithinSpotRatiosAtInterpolatedTimes(ShrimpFraction shrimpFraction) {

        int nDodCount = shrimpFraction.getIsotopicRatios().entrySet().iterator().next().getValue().getRatEqTime().size();

        for (int nDodNum = 0; nDodNum < nDodCount; nDodNum++) {
            // need to sort by reference material vs unknown
            StringBuilder dataLine = new StringBuilder();
            dataLine.append(shrimpFraction.getFractionID()).append(", ");
            dataLine.append(getFormattedDate(shrimpFraction.getDateTimeMilliseconds())).append(", ");
            dataLine.append(String.valueOf(nDodNum + 1)).append(", ");
            dataLine.append(shrimpFraction.isReferenceMaterial() ? "ref mat" : "unknown");

            for (Map.Entry<RawRatioNamesSHRIMP, IsotopeRatioModelSHRIMP> entry : shrimpFraction.getIsotopicRatios().entrySet()) {
                dataLine.append(", ").append(String.valueOf(entry.getValue().getRatEqTime().get(nDodNum)));
                dataLine.append(", ").append(String.valueOf(entry.getValue().getRatEqVal().get(nDodNum)));
                dataLine.append(", ").append(String.valueOf(entry.getValue().getRatEqErr().get(nDodNum)));
            }

            dataLine.append("\n");
            if (shrimpFraction.isReferenceMaterial()) {
                refMatWithinSpotRatiosAtInterpolatedTimes.append(dataLine);
            } else {
                unknownWithinSpotRatiosAtInterpolatedTimes.append(dataLine);
            }
        }
    }

    private static void reportIsotopeRatiosMeanAndSigma(ShrimpFraction shrimpFraction) {

        // need to sort by reference material vs unknown
        StringBuilder dataLine = new StringBuilder();
        dataLine.append(shrimpFraction.getFractionID()).append(", ");
        dataLine.append(getFormattedDate(shrimpFraction.getDateTimeMilliseconds())).append(", ");
        dataLine.append(shrimpFraction.isReferenceMaterial() ? "ref mat" : "unknown");

        for (Map.Entry<RawRatioNamesSHRIMP, IsotopeRatioModelSHRIMP> entry : shrimpFraction.getIsotopicRatios().entrySet()) {
            dataLine.append(", ").append(String.valueOf(entry.getValue().getRatioVal()));
            dataLine.append(", ").append(String.valueOf(entry.getValue().getRatioFractErr() * 100.0));
        }

        dataLine.append("\n");
        if (shrimpFraction.isReferenceMaterial()) {
            refMatMeanRatioAndSigmaPctPerIsotopicRatioPerAnalysis.append(dataLine);
        } else {
            unknownMeanRatioAndSigmaPctPerIsotopicRatioPerAnalysis.append(dataLine);
        }
    }

    private static void prepSpeciesReportFiles(ShrimpFraction shrimpFraction) throws IOException {
        String nameOfMount = shrimpFraction.getNameOfMount();
        String[] namesOfSpecies = shrimpFraction.getNamesOfSpecies();
        int countOfIntegrations = shrimpFraction.getPeakMeasurementsCount();

        totalIonCountsAtMassFile = new File(folderToWriteCalamariReportsPath + "Calamari_TotalIonCountsAtMass_for_" + nameOfMount + ".txt");
        StringBuilder header = new StringBuilder();
        header.append("Title, Date, Scan, Type, Dead_time_ns");

        for (String nameOfSpecies : namesOfSpecies) {
            header.append(", ").append(nameOfSpecies).append(".count_time_sec");
            for (int i = 0; i < countOfIntegrations; i++) {
                header.append(", ").append(nameOfSpecies).append(".").append(String.valueOf(i + 1));
            }
        }
        header.append("\n");

        Files.write(totalIonCountsAtMassFile.toPath(), header.toString().getBytes(UTF_8));

        totalSBMCountsAtMassFile = new File(folderToWriteCalamariReportsPath + "Calamari_TotalSBMCountsAtMass_for_" + nameOfMount + ".txt");
        header = new StringBuilder();
        header.append("Title, Date, Scan, Type, SBM_zero_cps");

        for (String nameOfSpecies : namesOfSpecies) {
            header.append(", ").append(nameOfSpecies).append(".count_time_sec");
            for (int i = 0; i < countOfIntegrations; i++) {
                header.append(", ").append(nameOfSpecies).append(".SBM.").append(String.valueOf(i + 1));
            }
        }
        header.append("\n");

        Files.write(totalSBMCountsAtMassFile.toPath(), header.toString().getBytes(UTF_8));

        totalCountsAtTimeStampAndTrimMass = new File(folderToWriteCalamariReportsPath + "Calamari_TotalCountsAtTimeStampAndTrimMass_for_" + nameOfMount + ".txt");
        header = new StringBuilder();
        header.append("Title, Date, Scan, Type");

        for (String nameOfSpecies : namesOfSpecies) {
            header.append(", ").append(nameOfSpecies).append(".Time");
            header.append(", ").append(nameOfSpecies).append(".TotalCounts");
            header.append(", ").append(nameOfSpecies).append(".1SigmaAbs");
            header.append(", ").append(nameOfSpecies).append(".TotalSBM");
            header.append(", ").append(nameOfSpecies).append(".TrimMass");
        }
        header.append("\n");

        Files.write(totalCountsAtTimeStampAndTrimMass.toPath(), header.toString().getBytes(UTF_8));

        totalCountsPerSecondPerSpeciesPerAnalysis = new File(folderToWriteCalamariReportsPath + "Calamari_TotalCountsPerSecondPerSpeciesPerAnalysis_for_" + nameOfMount + ".txt");
        header = new StringBuilder();
        header.append("Title, Date, Type");

        for (String nameOfSpecies : namesOfSpecies) {
            header.append(", ").append(nameOfSpecies).append(".TotalCps");
        }
        header.append("\n");

        Files.write(totalCountsPerSecondPerSpeciesPerAnalysis.toPath(), header.toString().getBytes(UTF_8));

        refMatFractionsTotalCountsPerSecondPerSpeciesPerAnalysis = new StringBuilder();
        unknownFractionsTotalCountsPerSecondPerSpeciesPerAnalysis = new StringBuilder();
    }

    private static void prepRatiosReportFiles(ShrimpFraction shrimpFraction) throws IOException {
        withinSpotRatiosAtInterpolatedTimes = new File(folderToWriteCalamariReportsPath + "Calamari_WithinSpotRatiosAtInterpolatedTimes_for_" + shrimpFraction.getNameOfMount() + ".txt");
        StringBuilder header = new StringBuilder();
        header.append("Title, Date, Ndod, Type");

        for (Map.Entry<RawRatioNamesSHRIMP, IsotopeRatioModelSHRIMP> entry : shrimpFraction.getIsotopicRatios().entrySet()) {
            header.append(", ").append(entry.getKey().getDisplayName().replaceAll(" ", "")).append(".InterpTIme");
            header.append(", ").append(entry.getKey().getDisplayName().replaceAll(" ", "")).append(".Value");
            header.append(", ").append(entry.getKey().getDisplayName().replaceAll(" ", "")).append(".1SigmaAbs");
        }

        header.append("\n");

        Files.write(withinSpotRatiosAtInterpolatedTimes.toPath(), header.toString().getBytes(UTF_8));

        refMatWithinSpotRatiosAtInterpolatedTimes = new StringBuilder();
        unknownWithinSpotRatiosAtInterpolatedTimes = new StringBuilder();

        meanRatioAndSigmaPctPerIsotopicRatioPerAnalysis = new File(folderToWriteCalamariReportsPath + "Calamari_MeanRatioAndSigmaPctPerIsotopicRatioPerAnalysis_for_" + shrimpFraction.getNameOfMount() + ".txt");
        header = new StringBuilder();
        header.append("Title, Date, Type");

        for (Map.Entry<RawRatioNamesSHRIMP, IsotopeRatioModelSHRIMP> entry : shrimpFraction.getIsotopicRatios().entrySet()) {
            header.append(", ").append(entry.getKey().getDisplayName().replaceAll(" ", "")).append(".Value");
            header.append(", ").append(entry.getKey().getDisplayName().replaceAll(" ", "")).append(".1SigmaPct");
        }

        header.append("\n");

        Files.write(meanRatioAndSigmaPctPerIsotopicRatioPerAnalysis.toPath(), header.toString().getBytes(UTF_8));

        refMatMeanRatioAndSigmaPctPerIsotopicRatioPerAnalysis = new StringBuilder();
        unknownMeanRatioAndSigmaPctPerIsotopicRatioPerAnalysis = new StringBuilder();

    }

    private static void finishSpeciesReportFiles() throws IOException {
        Files.write(totalCountsPerSecondPerSpeciesPerAnalysis.toPath(), refMatFractionsTotalCountsPerSecondPerSpeciesPerAnalysis.toString().getBytes(UTF_8), APPEND);
        Files.write(totalCountsPerSecondPerSpeciesPerAnalysis.toPath(), unknownFractionsTotalCountsPerSecondPerSpeciesPerAnalysis.toString().getBytes(UTF_8), APPEND);
    }

    private static void finishRatiosReportFiles() throws IOException {
        Files.write(withinSpotRatiosAtInterpolatedTimes.toPath(), refMatWithinSpotRatiosAtInterpolatedTimes.toString().getBytes(UTF_8), APPEND);
        Files.write(withinSpotRatiosAtInterpolatedTimes.toPath(), unknownWithinSpotRatiosAtInterpolatedTimes.toString().getBytes(UTF_8), APPEND);

        Files.write(meanRatioAndSigmaPctPerIsotopicRatioPerAnalysis.toPath(), refMatMeanRatioAndSigmaPctPerIsotopicRatioPerAnalysis.toString().getBytes(UTF_8), APPEND);
        Files.write(meanRatioAndSigmaPctPerIsotopicRatioPerAnalysis.toPath(), unknownMeanRatioAndSigmaPctPerIsotopicRatioPerAnalysis.toString().getBytes(UTF_8), APPEND);
    }

    private static String getFormattedDate(long milliseconds) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(milliseconds);
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");

        return dateFormat.format(calendar.getTime());
    }

    /**
     * @return the folderToWriteCalamariReports
     */
    public static File getFolderToWriteCalamariReports() {
        return folderToWriteCalamariReports;
    }

    /**
     * @param aFolderToWriteCalamariReports the folderToWriteCalamariReports to
     * set
     */
    public static void setFolderToWriteCalamariReports(File aFolderToWriteCalamariReports) {
        folderToWriteCalamariReports = aFolderToWriteCalamariReports;
    }

}
