package compiler.Parser.AST.ASTNodes.Expressions;

import compiler.Lexer.Symbol;
import compiler.Lexer.Token;

public abstract class Type {

    protected Symbol type; //Could be an identifier or a base type;

    public Type(Symbol type){
        if(!type.getType().equals(Token.Identifier) && !type.getType().equals(Token.BasedType)){
            throw new IllegalArgumentException("The base type must be an identifier or a base type");
        }
        this.type=Symbol.copy(type);
    }



}
