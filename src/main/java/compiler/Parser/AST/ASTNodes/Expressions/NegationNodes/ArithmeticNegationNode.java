package compiler.Parser.AST.ASTNodes.Expressions.NegationNodes;

import compiler.Parser.AST.ASTNodes.ExpressionStatement;
import compiler.Parser.AST.ASTNodes.Expressions.NegationNode;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class ArithmeticNegationNode extends NegationNode {
    public ArithmeticNegationNode(ExpressionStatement expression) {
        super(expression);
    }

    public String toString() {
        return "ArithmeticNegationNode{" +
                "- " + expression +
                '}';
    }

    public Double getValue(){
        throw new NotImplementedException();
        //return -1 * expression.getValue();
    }
}
