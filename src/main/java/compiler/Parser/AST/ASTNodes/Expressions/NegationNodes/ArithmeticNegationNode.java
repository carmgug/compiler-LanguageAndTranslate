package compiler.Parser.AST.ASTNodes.Expressions.NegationNodes;

import compiler.Exceptions.ParserExceptions.ParserException;
import compiler.Exceptions.SemanticException.SemanticErrorException;
import compiler.Parser.AST.ASTNodes.ExpressionStatement;
import compiler.Parser.AST.ASTNodes.Expressions.NegationNode;
import compiler.SemanticAnalysis.SymbolTable;
import compiler.SemanticAnalysis.Visitor.Visitor;

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

    public ExpressionStatement getExpression(){
        return expression;
    }

    @Override
    public void accept(Visitor visitor, SymbolTable symbolTable) throws SemanticErrorException {
        visitor.visit(this,symbolTable);
    }
}
