package compiler.Parser.AST.ASTNodes.Expressions;

import compiler.Lexer.Symbol;
import compiler.Lexer.Token;

public abstract class Type {

    protected Symbol type; //Could be an identifier or a base type;

    public Type(Symbol type){
        if(!type.getType().equals(Token.Identifier) &&
                !type.getType().equals(Token.IntType) &&
                !type.getType().equals(Token.FloatType) &&
                !type.getType().equals(Token.BoolType) &&
                !type.getType().equals(Token.StringType)
                 && !type.getType().equals(Token.Void)){
            throw new IllegalArgumentException("The base type must be an identifier or a base type");
        }
        this.type=Symbol.copy(type);
    }

    public Symbol getSymbol(){
        return this.type;
    }

    public String getNameofTheType(){
        return this.type.getValue();
    }

    public Token getTokenType(){
        return this.type.getType();
    }



    public String toString(){
        return type.getValue();
    }






}
