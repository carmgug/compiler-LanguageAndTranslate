package compiler.Parser.AST.ASTNodes.Expressions.Types;

import compiler.Lexer.Symbol;
import compiler.Lexer.Token;
import compiler.Parser.AST.ASTNodes.Expressions.Type;

public class BaseType extends Type {
    public BaseType(Symbol baseType) {
        super(baseType);
        if(!baseType.getType().equals(Token.BasedType)) throw new IllegalArgumentException("The type of BaseType must be a base type");
    }

    public String toString(){
        return type.getValue();
    }
}
