package compiler.Parser.AST.ASTNodes;

import compiler.Lexer.Symbol;
import compiler.Parser.AST.ASTNode;
import compiler.Parser.AST.ASTNodes.Expressions.Type;

public class VariableAssigment extends ASTNode {

    protected ExpressionStatement variable;
    protected ExpressionStatement right_side;

    public VariableAssigment(ExpressionStatement variable, ExpressionStatement right_side) {
        this.variable = variable;
        this.right_side = right_side;
    }

    public String toString() {
        return "VariableAssigment: {" +
                "Identifier: " + variable + "," +
                "Right side: " + right_side +
                "}";
    }

}