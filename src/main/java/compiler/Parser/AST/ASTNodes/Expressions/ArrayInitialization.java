package compiler.Parser.AST.ASTNodes.Expressions;

import compiler.Parser.AST.ASTNodes.ExpressionStatement;

public class ArrayInitialization extends ExpressionStatement {

    Type type;
    ExpressionStatement size;
    public ArrayInitialization(Type type, ExpressionStatement size) {
        this.type = type;
        this.size = size;
    }

    public String toString() {
        return "ArrayInitialization {" +
                    "type: " + type.toString() +","+
                    "size: " + size.toString()+
                "}";
    }
}
