package compiler.Parser.AST.ASTNodes.Expressions.NegationNodes;

import compiler.Parser.AST.ASTNodes.ExpressionStatement;
import compiler.Parser.AST.ASTNodes.Expressions.NegationNode;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class BooleanNegationNode extends NegationNode {
    public BooleanNegationNode(ExpressionStatement expression) {
        super(expression);
    }

    public String toString() {
        return "BooleanNegationNode{" +
                "not " + expression +
                '}';
    }


    public boolean getValue(){
        throw new NotImplementedException();
        //return -1 * expression.getValue();
    }


}
