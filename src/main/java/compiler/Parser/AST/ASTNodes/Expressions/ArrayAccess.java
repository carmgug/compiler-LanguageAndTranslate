package compiler.Parser.AST.ASTNodes.Expressions;

import compiler.Parser.AST.ASTNodes.ExpressionStatement;

public class ArrayAccess extends ExpressionStatement{

    private ExpressionStatement name_array;
    private ExpressionStatement index;

    public ArrayAccess(ExpressionStatement name_array, ExpressionStatement index){
        this.name_array=name_array;
        this.index=index;
    }

    public String toString() {
        return "ArrayAccess{"+ name_array +" ["+ index +"]}";
    }

}
