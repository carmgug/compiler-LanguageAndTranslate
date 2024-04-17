package compiler.Parser.AST.ASTNodes.Expressions;

import compiler.Exceptions.SemanticException.SemanticErrorException;
import compiler.Parser.AST.ASTNodes.ExpressionStatement;
import compiler.SemanticAnalysis.SymbolTable;
import compiler.SemanticAnalysis.Visitor.Visitor;

public class StructAccess extends ExpressionStatement {

    private ExpressionStatement leftPart;
    private ExpressionStatement rightPart;

    public StructAccess(ExpressionStatement leftPart, ExpressionStatement rightPart) {

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
}

