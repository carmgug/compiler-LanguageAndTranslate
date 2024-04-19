package compiler.Parser.AST.ASTNodes.Expressions;

import compiler.Exceptions.SemanticException.SemanticErrorException;
import compiler.Lexer.Symbol;
import compiler.Parser.AST.ASTNodes.ExpressionStatement;
import compiler.SemanticAnalysis.SymbolTable.SymbolTable;
import compiler.SemanticAnalysis.Visitor.Visitor;
import compiler.SemanticAnalysis.Visitor.VisitorType;

public class VariableReference extends ExpressionStatement {

    private final String identifier;
    private final int line;

    public VariableReference(Symbol identifier) {
        this.identifier = identifier.getValue();
        this.line=identifier.getLine();
    }

    public String getIdentifier() {
        return identifier;
    }
    public int getLine() {
        return line;
    }

    public String toString() {
        return "VariableReference: {"+
                "Identifier: " + identifier +
                "}";
    }

    @Override
    public void accept(Visitor visitor, SymbolTable symbolTable,SymbolTable structTable) throws SemanticErrorException {
        visitor.visit(this,symbolTable,structTable);
    }
}
