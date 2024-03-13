package compiler.Parser.AST.ASTNodes.Expressions.Types;

import compiler.Lexer.Symbol;
import compiler.Lexer.Token;
import compiler.Parser.AST.ASTNodes.Expressions.Type;

public class StructType extends Type {

    public StructType(Symbol identifier) {
        super(identifier);
        if(!identifier.getType().equals(Token.Identifier)) throw new IllegalArgumentException("The type of StructType must be an identifier");
    }

    public String toString(){
        return type.getValue();
    }
}
