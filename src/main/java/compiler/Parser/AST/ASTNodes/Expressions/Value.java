package compiler.Parser.AST.ASTNodes.Expressions;

import compiler.Lexer.Symbol;
import compiler.Parser.AST.ASTNodes.ExpressionStatement;

public class Value extends ExpressionStatement {
    Symbol value;

    public Value(Symbol value) {
        this.value = Symbol.copy(value);
    }

    public String toString() {
        return value.toString();
    }



}


