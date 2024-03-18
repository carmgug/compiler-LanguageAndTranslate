package compiler.Parser.AST.ASTNodes.Expressions;

import compiler.Parser.AST.ASTNodes.ExpressionStatement;

public class StructAccess extends ExpressionStatement {

    private ExpressionStatement leftPart;
    private ExpressionStatement rightPart;

    public StructAccess(ExpressionStatement leftPart, ExpressionStatement rightPart) {
        this.leftPart = leftPart;
        this.rightPart = rightPart;
    }
    public String toString() {
        return "StructAccess{leftPart=" + leftPart + ", rightPart=" + rightPart + "}";
    }
}

