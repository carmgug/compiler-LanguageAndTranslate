package compiler.Parser.AST.ASTNodes.Expressions;

import compiler.Exceptions.SemanticException.SemanticErrorException;
import compiler.Parser.AST.ASTNodes.ExpressionStatement;
import compiler.SemanticAnalysis.SymbolTable;
import compiler.SemanticAnalysis.Visitor.Visitor;

import java.util.ArrayList;
import java.util.Arrays;

public class ArrayValueDeclaration extends ExpressionStatement {

    ArrayList<ExpressionStatement> values;

    public ArrayValueDeclaration(ArrayList<ExpressionStatement> values) {
        this.values = values;
    }

    public ArrayList<ExpressionStatement> getValues(){
        return this.values;
    }

    public String toString() {
        return "ArrayValueDeclaration: {" + values.toString() + "}";
    }

    @Override
    public void accept(Visitor visitor, SymbolTable symbolTable) throws SemanticErrorException {
        visitor.visit(this,symbolTable);
    }
}