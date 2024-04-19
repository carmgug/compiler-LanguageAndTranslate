package compiler.Parser.AST.ASTNodes.Expressions.NegationNodes;

import compiler.Exceptions.ParserExceptions.ParserException;
import compiler.Exceptions.SemanticException.SemanticException;
import compiler.Parser.AST.ASTNodes.ExpressionStatement;
import compiler.Parser.AST.ASTNodes.Expressions.NegationNode;
import compiler.Parser.AST.ASTNodes.Expressions.Type;
import compiler.SemanticAnalysis.SymbolTable.SymbolTable;
import compiler.SemanticAnalysis.Visitor.Visitor;
import compiler.SemanticAnalysis.Visitor.VisitorType;

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
    public void accept(Visitor visitor, SymbolTable symbolTable,SymbolTable structTable) throws SemanticException {
        visitor.visit(this,symbolTable,structTable);
    }

    @Override
    public Type accept(VisitorType visitorType, SymbolTable symbolTable, SymbolTable structTable) throws SemanticException {
        return visitorType.visit(this,symbolTable,structTable);
    }
}
