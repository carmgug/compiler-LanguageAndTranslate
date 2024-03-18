package compiler.Parser.AST.ASTNodes.Expressions;

import compiler.Parser.AST.ASTNodes.ExpressionStatement;

import java.util.ArrayList;
import java.util.Arrays;

public class ArrayValueDeclaration extends ExpressionStatement {

    // Add your class properties and methods here
    ArrayList<ExpressionStatement> values;

    public ArrayValueDeclaration(ArrayList<ExpressionStatement> values) {
        this.values = values;
    }

    // Override toString() method if necessary
    public String toString() {
        return "{" + values.toString() + "}";
    }

}