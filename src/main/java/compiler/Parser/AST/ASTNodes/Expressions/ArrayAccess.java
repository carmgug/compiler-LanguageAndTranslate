package compiler.Parser.AST.ASTNodes.Expressions;

import compiler.CodeGenerator.CodeGenerationVisitor;
import compiler.CodeGenerator.EvaluateVisitor;
import compiler.CodeGenerator.ScopesTable;
import compiler.Exceptions.SemanticException.SemanticException;
import compiler.Parser.AST.ASTNodes.ExpressionStatement;
import compiler.SemanticAnalysis.SymbolTable.SymbolTable;
import compiler.SemanticAnalysis.Visitor.Visitor;
import compiler.SemanticAnalysis.Visitor.VisitorType;
import org.objectweb.asm.MethodVisitor;

public class ArrayAccess extends ExpressionStatement{

    private final ExpressionStatement array;
    private final ExpressionStatement index;
    private final int line;

    //field to store the type of the array
    //updated by semantic analysis
    private Type type;

    public void setType(Type type){
        this.type=type;
    }
    public Type getType(){
        return this.type;
    }



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

    public void accept(Visitor visitor, SymbolTable symbolTable,SymbolTable structTable) throws SemanticException {
        visitor.visit(this,symbolTable,structTable);
    }
    public Type accept(VisitorType visitorType, SymbolTable symbolTable, SymbolTable structTable) throws SemanticException {
        return visitorType.visit(this, symbolTable,structTable);
    }

    @Override
    public void accept(CodeGenerationVisitor codeGenerationVisitor, ScopesTable curr_scope, MethodVisitor mw) {
        throw new RuntimeException("Should not run!");
    }

    @Override
    public void accept(EvaluateVisitor visitor, ScopesTable curr_scope, MethodVisitor mw) {
        visitor.visit(this, curr_scope, mw);
    }

}
