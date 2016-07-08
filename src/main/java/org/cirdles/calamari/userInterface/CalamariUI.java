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
package org.cirdles.calamari.userInterface;

import com.apple.eawt.Application;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import javax.swing.UIManager;
import javax.xml.bind.JAXBException;
import org.cirdles.calamari.Calamari;
import org.cirdles.calamari.core.RawDataFileHandler;
import org.cirdles.calamari.core.ReportsEngine;
import org.cirdles.calamari.prawn.PrawnFileFilter;

/**
 *
 * @author James F. Bowring &lt;bowring at gmail.com&gt;
 */
public class CalamariUI extends javax.swing.JFrame {

    /**
     * Creates new form Calamari
     */
    public CalamariUI() {
        initComponents();
        initUI();
    }

    private void initUI() {

        this.setPreferredSize(new Dimension(700, 450));
        CalamariUI.setDefaultLookAndFeelDecorated(true);
        UIManager.getLookAndFeelDefaults().put("defaultFont", new Font("SansSerif", Font.PLAIN, 12));

        // check for MacOS
        String lcOSName = System.getProperty("os.name").toLowerCase();
        boolean MAC_OS_X = lcOSName.startsWith("mac os x");
        if (MAC_OS_X) {
            Application myAboutHandler = new MacOSAboutHandler();
        }

        // center me
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        int w = this.getSize().width;
        int h = this.getSize().height;
        int x = (dim.width - w) / 2;
        int y = (dim.height - h) / 2;

        this.setLocation(x, y);

        this.setTitle("Calamari Raw Data Processing for SHRIMP");
        calamariInfo.setText("Calamari version " + Calamari.VERSION + "   built on " + Calamari.RELEASE_DATE);
        updateCurrentPrawnFileLocation();
        updateReportsFolderLocationText();

        fileMenu.setVisible(false);
    }

    private void updateCurrentPrawnFileLocation() {
        currentPrawnFileLocation.setText(RawDataFileHandler.getCurrentPrawnFileLocation());
    }

    private void updateReportsFolderLocationText() {
        try {
            this.outputFolderLocation.setText(ReportsEngine.getFolderToWriteCalamariReports().getCanonicalPath());
        } catch (IOException iOException) {
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        basePane = new javax.swing.JLayeredPane();
        inputFileLocationLabel = new javax.swing.JLabel();
        outputFileLocationLabel = new javax.swing.JLabel();
        reduceDataButton = new javax.swing.JButton();
        selectReportsLocationButton = new javax.swing.JButton();
        outputFolderLocation = new javax.swing.JLabel();
        selectPrawnFileLocationButton = new javax.swing.JButton();
        currentPrawnFileLocation = new javax.swing.JLabel();
        calamariInfo = new javax.swing.JLabel();
        useSBM = new javax.swing.JCheckBox();
        userLinFits = new javax.swing.JCheckBox();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        openMenuItem = new javax.swing.JMenuItem();
        saveMenuItem = new javax.swing.JMenuItem();
        saveAsMenuItem = new javax.swing.JMenuItem();
        exitMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        calamariHelpMenuItem = new javax.swing.JMenuItem();
        aboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(700, 450));

        basePane.setBackground(new java.awt.Color(255, 231, 228));
        basePane.setOpaque(true);
        basePane.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        inputFileLocationLabel.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        inputFileLocationLabel.setText("Prawn file path:");
        basePane.add(inputFileLocationLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 110, 250, 30));

        outputFileLocationLabel.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        outputFileLocationLabel.setText("CalamariReports folder location:");
        basePane.add(outputFileLocationLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 295, -1, 30));

        reduceDataButton.setFont(new java.awt.Font("Arial", 0, 16)); // NOI18N
        reduceDataButton.setText("Reduce Data and Produce Reports");
        reduceDataButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reduceDataButtonActionPerformed(evt);
            }
        });
        basePane.add(reduceDataButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 365, 380, 30));

        selectReportsLocationButton.setFont(new java.awt.Font("Arial", 0, 16)); // NOI18N
        selectReportsLocationButton.setText("Select location for CalamariReports Folder");
        selectReportsLocationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectReportsLocationButtonActionPerformed(evt);
            }
        });
        basePane.add(selectReportsLocationButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 295, 370, 30));

        outputFolderLocation.setBackground(new java.awt.Color(255, 255, 255));
        outputFolderLocation.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        outputFolderLocation.setText("path");
        outputFolderLocation.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        outputFolderLocation.setOpaque(true);
        basePane.add(outputFolderLocation, new org.netbeans.lib.awtextra.AbsoluteConstraints(45, 330, 630, -1));

        selectPrawnFileLocationButton.setFont(new java.awt.Font("Arial", 0, 16)); // NOI18N
        selectPrawnFileLocationButton.setText("Select Prawn File");
        selectPrawnFileLocationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectPrawnFileLocationButtonActionPerformed(evt);
            }
        });
        basePane.add(selectPrawnFileLocationButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 110, 370, 30));

        currentPrawnFileLocation.setBackground(new java.awt.Color(255, 255, 255));
        currentPrawnFileLocation.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        currentPrawnFileLocation.setText("path");
        currentPrawnFileLocation.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        currentPrawnFileLocation.setOpaque(true);
        basePane.add(currentPrawnFileLocation, new org.netbeans.lib.awtextra.AbsoluteConstraints(45, 145, 630, -1));

        calamariInfo.setBackground(new java.awt.Color(255, 8, 9));
        calamariInfo.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        calamariInfo.setForeground(new java.awt.Color(255, 255, 255));
        calamariInfo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        calamariInfo.setText("jLabel1");
        calamariInfo.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));
        calamariInfo.setOpaque(true);
        basePane.add(calamariInfo, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, 630, -1));

        useSBM.setBackground(new java.awt.Color(255, 231, 228));
        useSBM.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        useSBM.setSelected(true);
        useSBM.setText("use SBM");
        useSBM.setOpaque(true);
        basePane.add(useSBM, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 190, -1, -1));

        userLinFits.setBackground(new java.awt.Color(255, 231, 228));
        userLinFits.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        userLinFits.setText("user LinFits");
        userLinFits.setOpaque(true);
        basePane.add(userLinFits, new org.netbeans.lib.awtextra.AbsoluteConstraints(145, 190, -1, -1));

        fileMenu.setMnemonic('f');
        fileMenu.setText("File");

        openMenuItem.setMnemonic('o');
        openMenuItem.setText("Open");
        fileMenu.add(openMenuItem);

        saveMenuItem.setMnemonic('s');
        saveMenuItem.setText("Save");
        fileMenu.add(saveMenuItem);

        saveAsMenuItem.setMnemonic('a');
        saveAsMenuItem.setText("Save As ...");
        saveAsMenuItem.setDisplayedMnemonicIndex(5);
        fileMenu.add(saveAsMenuItem);

        exitMenuItem.setMnemonic('x');
        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setMnemonic('h');
        helpMenu.setText("Help");

        calamariHelpMenuItem.setText("Calamari Help");
        helpMenu.add(calamariHelpMenuItem);

        aboutMenuItem.setMnemonic('a');
        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(basePane)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(basePane)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitMenuItemActionPerformed

    private void reduceDataButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reduceDataButtonActionPerformed
        try {
            RawDataFileHandler.writeReportsFromPrawnFile(RawDataFileHandler.getCurrentPrawnFileLocation(), useSBM.isSelected(), userLinFits.isSelected());
        } catch (IOException | JAXBException exception) {
            System.out.println("Exception extracting data: " + exception.getStackTrace()[0].toString());
        }
    }//GEN-LAST:event_reduceDataButtonActionPerformed

    private void selectReportsLocationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectReportsLocationButtonActionPerformed
        File reportFolder = FileHelper.AllPlatformGetFolder("Select location to write reports", ReportsEngine.getFolderToWriteCalamariReports());
        if (reportFolder != null) {
            ReportsEngine.setFolderToWriteCalamariReports(reportFolder);
            updateReportsFolderLocationText();
        }
    }//GEN-LAST:event_selectReportsLocationButtonActionPerformed

    private void selectPrawnFileLocationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectPrawnFileLocationButtonActionPerformed

        File prawnFile = FileHelper.AllPlatformGetFile(//
                "Select Prawn file", //
                new File(RawDataFileHandler.getCurrentPrawnFileLocation()), //
                "*.xml", new PrawnFileFilter(), false, this)[0];
        if (prawnFile != null) {
            try {
                RawDataFileHandler.setCurrentPrawnFileLocation(prawnFile.getCanonicalPath());
                updateCurrentPrawnFileLocation();
            } catch (IOException iOException) {
            }
        }


    }//GEN-LAST:event_selectPrawnFileLocationButtonActionPerformed

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
        AboutBox.getInstance().setVisible(true);
    }//GEN-LAST:event_aboutMenuItemActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JLayeredPane basePane;
    private javax.swing.JMenuItem calamariHelpMenuItem;
    private javax.swing.JLabel calamariInfo;
    private javax.swing.JLabel currentPrawnFileLocation;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JLabel inputFileLocationLabel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JLabel outputFileLocationLabel;
    private javax.swing.JLabel outputFolderLocation;
    private javax.swing.JButton reduceDataButton;
    private javax.swing.JMenuItem saveAsMenuItem;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JButton selectPrawnFileLocationButton;
    private javax.swing.JButton selectReportsLocationButton;
    private javax.swing.JCheckBox useSBM;
    private javax.swing.JCheckBox userLinFits;
    // End of variables declaration//GEN-END:variables

}
