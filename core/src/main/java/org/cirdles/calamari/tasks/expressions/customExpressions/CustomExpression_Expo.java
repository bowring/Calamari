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
package org.cirdles.calamari.tasks.expressions.customExpressions;

import org.cirdles.calamari.tasks.expressions.ExpressionTree;
import org.cirdles.calamari.tasks.expressions.ExpressionTreeBuilderInterface;
import org.cirdles.calamari.tasks.expressions.ExpressionTreeInterface;
import org.cirdles.calamari.tasks.expressions.constants.ConstantNode;
import org.cirdles.calamari.tasks.expressions.functions.Function;
import org.cirdles.calamari.tasks.expressions.variables.VariableNode;

/**
 *
 * @author James F. Bowring
 */
public class CustomExpression_Expo {

    /**
     * Squid Excel format is robreg(["LnUOU"],["LnPbRU"],false,true)
     */
    public final static ExpressionTreeInterface EXPRESSION = new ExpressionTree("Expo");

    static {
        ((ExpressionTreeBuilderInterface) EXPRESSION)
                .addChild(0, new VariableNode(CustomExpression_LnUO_U.EXPRESSION.getName()));
        ((ExpressionTreeBuilderInterface) EXPRESSION)
                .addChild(new VariableNode(CustomExpression_LnPbR_U.EXPRESSION.getName()));
        ((ExpressionTreeBuilderInterface) EXPRESSION).addChild(new ConstantNode("false", 0));
        ((ExpressionTreeBuilderInterface) EXPRESSION).addChild(new ConstantNode("false", 0));
        ((ExpressionTreeBuilderInterface) EXPRESSION).setOperation(Function.robReg());

        ((ExpressionTree) EXPRESSION).setRootExpressionTree(true);
        ((ExpressionTree) EXPRESSION).setSquidSwitchSCSummaryCalculation(true);
        ((ExpressionTree) EXPRESSION).setSquidSwitchSTReferenceMaterialCalculation(true);
        ((ExpressionTree) EXPRESSION).setSquidSwitchSAUnknownCalculation(false);
    }
}
