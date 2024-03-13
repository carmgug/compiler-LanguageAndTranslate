package compiler.Parser.AST.ASTNodes.Expressions.Types;

import compiler.Lexer.Symbol;
public class ArrayStructType extends StructType{
    public ArrayStructType(Symbol identifier) {
        super(identifier);
    }

    public String toString() {
        return super.toString()+"[]";
    }
}
