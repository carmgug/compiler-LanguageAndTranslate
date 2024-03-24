package compiler.Parser.AST.ASTNodes.Expressions;

import compiler.Lexer.Symbol;
import compiler.Parser.AST.ASTNodes.ExpressionStatement;

public class VariableReference extends ExpressionStatement {

    private String identifier;

    public VariableReference(Symbol identifier) {

        this.identifier = identifier.getValue();
    }

    public String getIdentifier() {
        return identifier;
    }

    public String toString() {
        return "VariableReference: {"+
                "Identifier: " + identifier +
                "}";
    }
}
