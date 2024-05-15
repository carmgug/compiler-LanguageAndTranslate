package compiler.Parser.AST.ASTNodes;

import compiler.CodeGenerator.CodeGenerationVisitor;
import compiler.CodeGenerator.EvaluateVisitor;
import compiler.CodeGenerator.ScopesTable;
import compiler.Exceptions.SemanticException.SemanticException;
import compiler.Parser.AST.ASTNode;
import compiler.Parser.AST.ASTNodes.Expressions.Type;
import compiler.Parser.AST.ASTNodes.Expressions.VariableReference;
import compiler.SemanticAnalysis.SymbolTable.SymbolTable;
import compiler.SemanticAnalysis.Visitor.Visitor;
import compiler.SemanticAnalysis.Visitor.VisitorType;
import org.objectweb.asm.MethodVisitor;

public class VariableDeclaration extends ASTNode {

    protected Type type;
    protected VariableReference variable;


    public VariableDeclaration(Type type, VariableReference identifier){
        this.type=type;
        this.variable=identifier;
    }


    public Type getType(){
        return this.type;
    }

    public VariableReference getIdentifier(){
        return this.variable;
    }

    public String getNameOfTheVariable(){
        return this.variable.getIdentifier();
    }
    public int getLine(){
        return this.variable.getLine();
    }

    public String toString(){
        return "VariableDeclaration: {"+
                "Type: " + type + ","+
                "Identifier: " + variable.getIdentifier() +
                "}";
    }

    @Override
    public void accept(Visitor visitor,SymbolTable symbolTable,SymbolTable structTable) throws SemanticException {
        visitor.visit(this,symbolTable,structTable);
    }

    @Override
    public Type accept(VisitorType visitorType, SymbolTable symbolTable, SymbolTable structTable) throws SemanticException {
        return null;
    }

    @Override
    public void accept(CodeGenerationVisitor codeGenerationVisitor, ScopesTable curr_scope, MethodVisitor mw) {
        codeGenerationVisitor.visit(this,curr_scope,mw);
    }

    @Override
    public void accept(EvaluateVisitor visitor, ScopesTable curr_scope, MethodVisitor mw) {
        throw new RuntimeException("Should not run!");
    }
}
