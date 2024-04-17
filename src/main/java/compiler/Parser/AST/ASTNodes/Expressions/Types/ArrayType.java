package compiler.Parser.AST.ASTNodes.Expressions.Types;

import compiler.Lexer.Symbol;
import compiler.Lexer.Token;
import compiler.Parser.AST.ASTNodes.Expressions.Type;

public class ArrayType extends BaseType {


    public ArrayType(Symbol baseType) {
        super(baseType);
        if(!baseType.getType().equals(Token.BasedType)) throw new IllegalArgumentException("The type of ArrayType must be a base type");
    }

    @Override
    public String toString() {
        return "Array of "+super.toString();
    }

    public String getType(){
        return super.toString();
    }
}
