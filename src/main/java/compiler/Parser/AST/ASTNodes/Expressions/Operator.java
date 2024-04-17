package compiler.Parser.AST.ASTNodes.Expressions;

import compiler.Lexer.Symbol;

public class Operator {

    protected Symbol operator;

    public Operator(Symbol operator){
        this.operator=operator;
    }

    public String toString() {
        return "Operator: {"+
                "Type: " + operator.getType() + ","+
                "Value: " + operator.getValue() +
                "}";
    }

    public Symbol getSymbol(){return this.operator;}
}
