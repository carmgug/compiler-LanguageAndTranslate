package compiler.Parser.AST.ASTNodes.Expressions;

import compiler.Parser.AST.ASTNodes.ExpressionStatement;

public abstract class NegationNode extends ExpressionStatement {
    protected ExpressionStatement expression;

    public NegationNode(ExpressionStatement expression) {
        this.expression = expression;
    }





}
