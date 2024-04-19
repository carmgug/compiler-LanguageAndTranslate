package compiler.Parser.AST.ASTNodes.Expressions;

import compiler.Exceptions.SemanticException.SemanticException;
import compiler.Lexer.Symbol;
import compiler.Parser.AST.ASTNodes.ExpressionStatement;
import compiler.SemanticAnalysis.SymbolTable.SymbolTable;
import compiler.SemanticAnalysis.Visitor.Visitor;
import compiler.SemanticAnalysis.Visitor.VisitorType;

public class BinaryExpression extends ExpressionStatement {

    Operator operator;
    ExpressionStatement right;
    ExpressionStatement left;


    public BinaryExpression(ExpressionStatement left, Symbol operator, ExpressionStatement right) {
        this.left=left;
        this.operator = new Operator(operator);
        this.right = right;
    }

    private String toString(int indent) {
        String indentation = generateIndentation(indent);
        return "BinaryExpression{\n" +
                indentation + "\tleft=" + (left instanceof BinaryExpression ? ((BinaryExpression) left).toString(indent + 1) : left) + "\n" +
                indentation + "\toperator=" + operator + "\n" +
                indentation + "\tright=" + (right instanceof BinaryExpression ? ((BinaryExpression) right).toString(indent + 1) : right) + "\n" +
                indentation + "}";
    }

    public String toString() {
        return "BinaryExpression : { " +
                "operator : " + operator+
                ", right : " + right +
                ", left : " + left +
                '}';
    }

    public ExpressionStatement getRight(){return this.right;}

    public ExpressionStatement getLeft(){return this.left;}

    public Operator getOperator(){return this.operator;}

    @Override
    public void accept(Visitor visitor, SymbolTable symbolTable,SymbolTable structTable) throws SemanticException {
        visitor.visit(this,symbolTable,structTable);
    }
    public Type accept(VisitorType visitorType, SymbolTable symbolTable, SymbolTable structTable) throws SemanticException {
        return visitorType.visit(this, symbolTable,structTable);
    }


}
