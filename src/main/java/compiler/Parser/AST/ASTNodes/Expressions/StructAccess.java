package compiler.Parser.AST.ASTNodes.Expressions;

import compiler.Lexer.SpeciefiedSymbol.Identifier;

import compiler.Parser.AST.ASTNodes.ExpressionStatement;

import java.util.LinkedList;

public class StructAccess extends ExpressionStatement{
    private LinkedList<Value> identifiers;

    public StructAccess(LinkedList<Value> identifiers){
        this.identifiers=identifiers;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Value v : identifiers) {
            sb.append(v.getSymbol().getValue());
            sb.append(".");
        }
        sb.deleteCharAt(sb.length() - 1); //delete the last .
        return "StructAccess{" +
                sb.toString() +
                '}';
    }
}
