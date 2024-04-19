package compiler.Parser.AST.ASTNodes.Expressions;

import compiler.Exceptions.SemanticException.SemanticException;
import compiler.Parser.AST.ASTNodes.ExpressionStatement;
import compiler.SemanticAnalysis.SymbolTable.SymbolTable;
import compiler.SemanticAnalysis.Visitor.Visitor;
import compiler.SemanticAnalysis.Visitor.VisitorType;

import java.util.ArrayList;

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
    public void accept(Visitor visitor, SymbolTable symbolTable,SymbolTable structTable) throws SemanticException {
        visitor.visit(this,symbolTable,structTable);
    }

    public Type accept(VisitorType visitorType, SymbolTable symbolTable, SymbolTable structTable) throws SemanticException {
        return visitorType.visit(this, symbolTable,structTable);
    }
}