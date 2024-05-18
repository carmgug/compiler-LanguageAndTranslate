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

import java.util.ArrayList;

public class ArrayValueDeclaration extends ExpressionStatement {

    ArrayList<ExpressionStatement> values;

    //Value updated during semantic analysis
    Type type;

    public void setType(Type type){
        this.type = type;
    }

    public Type getType(){
        return this.type;
    }


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

    @Override
    public void accept(CodeGenerationVisitor codeGenerationVisitor, ScopesTable curr_scope, MethodVisitor mw) {
        throw new RuntimeException("Should not run!");
    }

    @Override
    public void accept(EvaluateVisitor visitor, ScopesTable curr_scope, MethodVisitor mw) {
        visitor.visit(this, curr_scope,mw);
    }
}