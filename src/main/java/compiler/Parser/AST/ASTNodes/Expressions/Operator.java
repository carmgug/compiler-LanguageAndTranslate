package compiler.Parser.AST.ASTNodes.Expressions;

import compiler.Lexer.Symbol;

public class Operator {


    int precendence;
    Symbol operator;

    public Operator(Symbol operator) {
        this.operator=Symbol.copy(operator);
    }

    public String toString() {
        return operator.toString();
    }
}
