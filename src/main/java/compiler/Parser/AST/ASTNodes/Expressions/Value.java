package compiler.Parser.AST.ASTNodes.Expressions;

import compiler.Exceptions.SemanticException.SemanticErrorException;
import compiler.Lexer.Symbol;
import compiler.Parser.AST.ASTNodes.ExpressionStatement;
import compiler.SemanticAnalysis.SymbolTable;
import compiler.SemanticAnalysis.Visitor.Visitor;

public class Value extends ExpressionStatement {
    Symbol value;

    public Value(Symbol value) {
        this.value = Symbol.copy(value);
    }

    public String toString() {
        return "Value: {"+
                "Type: " + value.getType() + ","+
                "Value: " + value.getValue() +
                "}";
    }

    public Symbol getSymbol() {
        return value;
    }


    @Override
    public void accept(Visitor visitor, SymbolTable symbolTable,SymbolTable structTable) throws SemanticErrorException {
        visitor.visit(this,symbolTable,structTable);
    }
}


