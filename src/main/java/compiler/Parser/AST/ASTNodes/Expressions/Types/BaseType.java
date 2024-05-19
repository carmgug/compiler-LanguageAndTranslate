package compiler.Parser.AST.ASTNodes.Expressions.Types;

import compiler.Lexer.Symbol;
import compiler.Lexer.Token;
import compiler.Parser.AST.ASTNodes.Expressions.Type;

public class BaseType extends Type {


    public BaseType(Symbol baseType) {
        super(baseType);
        if(!type.getType().equals(Token.IntType) &&
                !type.getType().equals(Token.FloatType) &&
                !type.getType().equals(Token.BoolType)&&
                !type.getType().equals(Token.StringType)){
            throw new IllegalArgumentException("The type of BaseType must be a base type (IntType, FloatNumber, String, BooleanValue)");
        }
    }

    public String toString(){
        return type.getValue();
    }

    public boolean equals(Object obj){
        if(!(obj instanceof BaseType)) return false;
        if(obj instanceof ArrayType) return false;
        BaseType baseType_2=(BaseType)obj;
        return this.type.getType().equals(baseType_2.getSymbol().getType());
    }


}
