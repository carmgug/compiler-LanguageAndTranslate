package compiler.Parser.AST.ASTNodes.Expressions.Types;

import compiler.Lexer.Symbol;

// ArrayStructType is a type that represents an array of structs
public class ArrayStructType extends StructType{
    public ArrayStructType(Symbol identifier) {
        super(identifier);
    }

    public String toString() {
        return "Array of "+super.toString();
    }


    public boolean equals(Object obj){
        if(!(obj instanceof ArrayStructType)) return false;
        ArrayStructType arrayStructType_2=(ArrayStructType)obj;
        return this.type.getType().equals(arrayStructType_2.getSymbol().getType());
    }
}
