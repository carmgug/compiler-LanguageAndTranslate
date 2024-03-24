package compiler.Parser.AST.ASTNodes.Expressions;

import compiler.Parser.AST.ASTNodes.ExpressionStatement;

public class ArrayAccess extends ExpressionStatement{

    private ExpressionStatement array;
    private ExpressionStatement index;

    public ArrayAccess(ExpressionStatement array, ExpressionStatement index){
        this.array=array;
        this.index=index;
    }

    public String toString() {
        return "ArrayAccess{"+
                    "Array: "+array +"," +
                    "Index: "+index +
                "}";
    }

}
