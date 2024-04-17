package compiler.Parser.AST.ASTNodes.Expressions;

import compiler.Exceptions.SemanticException.SemanticErrorException;
import compiler.Lexer.Symbol;
import compiler.Parser.AST.ASTNodes.ExpressionStatement;
import compiler.SemanticAnalysis.SymbolTable;
import compiler.SemanticAnalysis.Visitor.Visitor;

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

    @Override
    public void accept(Visitor visitor, SymbolTable symbolTable) throws SemanticErrorException {
        visitor.visit(this,symbolTable);
    }
}
