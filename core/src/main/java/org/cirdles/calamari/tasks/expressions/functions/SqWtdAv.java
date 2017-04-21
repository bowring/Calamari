/* 
 * Copyright 2006-2017 CIRDLES.org.
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
package org.cirdles.calamari.tasks.expressions.functions;

import java.util.List;
import org.apache.commons.math3.distribution.FDistribution;
import org.apache.commons.math3.distribution.TDistribution;
import org.cirdles.calamari.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.calamari.tasks.expressions.ExpressionTreeInterface;

/**
 *
 * @author James F. Bowring
 */
public class SqWtdAv extends Function {

    /**
     * Provides the basic functionality of Squid's sqWtdAv by calculating
     * WeightedAverage and returning intMean, intSigmaMean, MSWD, probability,
     * intErr68, intMeanErr95 and encoding the labels for each cell of the
     * values array produced by eval2Array.
     *
     * @see
     * https://github.com/CIRDLES/LudwigLibrary/blob/master/vbaCode/squid2.5Basic/MathUtils.bas
     * @see
     * https://github.com/CIRDLES/LudwigLibrary/blob/master/vbaCode/isoplot3Basic/Means.bas
     */
    public SqWtdAv() {
        name = "sqWtdAv";
        argumentCount = 1;
        precedence = 4;
        rowCount = 1;
        colCount = 6;
        labelsForValues = new String[][]{{"intMean", "intSigmaMean", "MSWD", "probability", "intErr68", "intMeanErr95"}};
    }

    /**
     * Requires that child 0 is a VariableNode that evaluates to a double array
     * with one column and a row for each member of shrimpFractions and that
     * child 1 is a ConstantNode that evaluates to an integer.
     *
     * @param childrenET list containing child 0 and child 1
     * @param shrimpFractions a list of shrimpFractions
     * @return the double[1][6] array of intMean, intSigmaMean, MSWD,
     * probability, intErr68, intMeanErr95
     */
    @Override
    public double[][] eval2Array(
            List<ExpressionTreeInterface> childrenET, List<ShrimpFractionExpressionInterface> shrimpFractions) {

        double[][] retVal;
        try {
            double[][] valuesAndUncertainties = childrenET.get(0).eval2Array(shrimpFractions);
            double[] variableValues = transposeColumnVector(valuesAndUncertainties, 0);
            double[] uncertaintyValues = transposeColumnVector(valuesAndUncertainties, 1);
            double[] weightedAverage = org.cirdles.ludwig.isoplot3.Means.weightedAverage(variableValues, uncertaintyValues);
            retVal = new double[][]{weightedAverage};
        } catch (ArithmeticException e) {
            retVal = new double[][]{{0.0, 0.0, 0.0, 0.0, 0.0, 0.0}};
        }

        return retVal;
    }

    /**
     *
     * @param childrenET the value of childrenET
     * @return
     */
    @Override
    public String toStringMathML(List<ExpressionTreeInterface> childrenET) {
        String retVal
                = "<mrow>"
                + "<mi>" + name + "</mi>"
                + "<mfenced>";

        for (int i = 0; i < childrenET.size(); i++) {
            retVal += toStringAnotherExpression(childrenET.get(i)) + "&nbsp;\n";
        }

        retVal += "</mfenced></mrow>\n";

        return retVal;
    }
}
