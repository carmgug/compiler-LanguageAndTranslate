package compiler.Parser.AST.ASTNodes.Expressions;

import compiler.Exceptions.SemanticException.SemanticErrorException;
import compiler.Parser.AST.ASTNodes.ExpressionStatement;
import compiler.SemanticAnalysis.SymbolTable;
import compiler.SemanticAnalysis.Visitor.Visitor;

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

    public ExpressionStatement getArray(){return  this.array;}

    public ExpressionStatement getIndex(){return  this.index;}

    public void accept(Visitor visitor, SymbolTable symbolTable,SymbolTable structTable) throws SemanticErrorException {
        visitor.visit(this,symbolTable,structTable);
    }

}
