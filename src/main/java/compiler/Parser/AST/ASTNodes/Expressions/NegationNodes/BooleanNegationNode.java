package compiler.Parser.AST.ASTNodes.Expressions.NegationNodes;

import compiler.Exceptions.ParserExceptions.ParserException;
import compiler.Parser.AST.ASTNodes.ExpressionStatement;
import compiler.Parser.AST.ASTNodes.Expressions.NegationNode;

public class BooleanNegationNode extends NegationNode {
    public BooleanNegationNode(ExpressionStatement expression) {
        super(expression);
    }

    public String toString() {
        return "BooleanNegationNode {" +
                "Negation: !," +
                "expression :" + expression +
                '}';
    }


    public boolean getValue() throws ParserException {
        throw new ParserException("Not Implemented");
        //return -1 * expression.getValue();
    }


}
