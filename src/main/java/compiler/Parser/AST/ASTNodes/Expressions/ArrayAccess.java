package compiler.Parser.AST.ASTNodes.Expressions;

import compiler.Exceptions.SemanticException.SemanticErrorException;
import compiler.Parser.AST.ASTNodes.ExpressionStatement;
import compiler.SemanticAnalysis.SymbolTable.SymbolTable;
import compiler.SemanticAnalysis.Visitor.Visitor;
import compiler.SemanticAnalysis.Visitor.VisitorType;

public class ArrayAccess extends ExpressionStatement{

    private final ExpressionStatement array;
    private final ExpressionStatement index;
    private final int line;

    public ArrayAccess(ExpressionStatement array, ExpressionStatement index,int line){
        this.array=array;
        this.index=index;
        this.line=line;
    }

    public String toString() {
        return "ArrayAccess{"+
                    "Array: "+array +"," +
                    "Index: "+index +
                "}";
    }

    public int getLine(){
        return line;
    }

    public ExpressionStatement getArray(){return  this.array;}

    public ExpressionStatement getIndex(){return  this.index;}

    public void accept(Visitor visitor, SymbolTable symbolTable,SymbolTable structTable) throws SemanticErrorException {
        visitor.visit(this,symbolTable,structTable);
    }
    public Type accept(VisitorType visitorType, SymbolTable symbolTable, SymbolTable structTable) throws SemanticErrorException {
        return visitorType.visit(this, symbolTable,structTable);
    }

}
