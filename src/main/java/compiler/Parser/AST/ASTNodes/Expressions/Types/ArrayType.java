package compiler.Parser.AST.ASTNodes.Expressions.Types;

import compiler.Lexer.Symbol;
import compiler.Lexer.Token;
import compiler.Parser.AST.ASTNodes.Expressions.Type;

public class ArrayType extends BaseType {


    public ArrayType(Symbol baseType) {
        super(baseType);
        if(!baseType.getType().equals(Token.IntType) &&
                !baseType.getType().equals(Token.FloatType) &&
                !baseType.getType().equals(Token.BoolType) &&
                !baseType.getType().equals(Token.StringType)){
            throw new IllegalArgumentException("The type of ArrayType must be a base type");
        }
    }

    @Override
    public String toString() {
        return "Array of "+super.toString();
    }

    public boolean equals(Object obj){
        if(!(obj instanceof ArrayType)) return false;
        ArrayType arrayType_2=(ArrayType)obj;
        return this.type.getType().equals(arrayType_2.getSymbol().getType());
    }




}
