package compiler.Parser.AST.ASTNodes.Expressions;

import compiler.Parser.AST.ASTNodes.ExpressionStatement;

public class NegativeNode extends ExpressionStatement{
    ExpressionStatement expression;

    public NegativeNode(ExpressionStatement expression) {
        this.expression = expression;
    }

    public String toString() {
        return "Negative Expr= -" + expression ;
    }
}
