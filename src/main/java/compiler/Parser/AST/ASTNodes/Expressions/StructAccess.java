package compiler.Parser.AST.ASTNodes.Expressions;

import compiler.Exceptions.SemanticException.SemanticErrorException;
import compiler.Parser.AST.ASTNodes.ExpressionStatement;
import compiler.SemanticAnalysis.SymbolTable.SymbolTable;
import compiler.SemanticAnalysis.Visitor.Visitor;
import compiler.SemanticAnalysis.Visitor.VisitorType;

public class StructAccess extends ExpressionStatement {

    private final ExpressionStatement leftPart;
    private final ExpressionStatement rightPart;
    private final int line;

    public StructAccess(ExpressionStatement leftPart, ExpressionStatement rightPart,int line) {
        this.line= line;
        this.leftPart = leftPart;
        this.rightPart = rightPart;
    }
    public String toString() {
        return
                "StructAccess{" +
                        "leftPart: " + leftPart + ","+
                        "rightPart: " + rightPart +
                "}";
    }

    public int getLine() {
        return line;
    }

    public ExpressionStatement getLeftPart() {
        return leftPart;
    }

    public ExpressionStatement getRightPart() {
        return rightPart;
    }

    @Override
    public void accept(Visitor visitor, SymbolTable symbolTable,SymbolTable structTable) throws SemanticErrorException {
        visitor.visit(this,symbolTable,structTable);
    }

    @Override
    public Type accept(VisitorType visitorType, SymbolTable symbolTable, SymbolTable structTable) throws SemanticErrorException {
        return visitorType.visit(this,symbolTable,structTable);
    }
}

