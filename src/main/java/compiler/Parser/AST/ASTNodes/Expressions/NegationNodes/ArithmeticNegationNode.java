package compiler.Parser.AST.ASTNodes.Expressions.NegationNodes;

import compiler.Exceptions.ParserExceptions.ParserException;
import compiler.Parser.AST.ASTNodes.ExpressionStatement;
import compiler.Parser.AST.ASTNodes.Expressions.NegationNode;

public class ArithmeticNegationNode extends NegationNode {
    public ArithmeticNegationNode(ExpressionStatement expression) {
        super(expression);
    }

    public String toString() {
        return "ArithmeticNegationNode {" +
                "Negation: -," +
                "expression :" + expression +
                '}';
    }

    public Double getValue() throws ParserException {
        throw new ParserException("Not Implemente yet");
        //return -1 * expression.getValue();
    }
}
