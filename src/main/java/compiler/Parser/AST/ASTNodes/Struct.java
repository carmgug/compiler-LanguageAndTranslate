package compiler.Parser.AST.ASTNodes;

import compiler.CodeGenerator.CodeGenerationVisitor;
import compiler.CodeGenerator.EvaluateVisitor;
import compiler.CodeGenerator.ScopesTable;
import compiler.Exceptions.SemanticException.SemanticException;
import compiler.Lexer.Symbol;
import compiler.Parser.AST.ASTNode;
import compiler.Parser.AST.ASTNodes.Expressions.Type;
import compiler.SemanticAnalysis.SymbolTable.SymbolTable;
import compiler.SemanticAnalysis.Visitor.Visitor;
import compiler.SemanticAnalysis.Visitor.VisitorType;
import org.objectweb.asm.MethodVisitor;

import java.util.ArrayList;

public class Struct extends ASTNode {

    Symbol identifier;
    ArrayList<VariableDeclaration> variableDeclarations;

    public Struct(Symbol identifier, ArrayList<VariableDeclaration> variableDeclarations){
        this.identifier=identifier;
        this.variableDeclarations = variableDeclarations;
    }

    public Struct(){
        this.identifier=null;
        this.variableDeclarations =null;
    }

    @Override
    public void accept(Visitor visitor, SymbolTable symbolTable, SymbolTable structTable) throws SemanticException {
        visitor.visit(this,symbolTable,structTable);

    }



    public Symbol getIdentifier(){
        return this.identifier;
    }
    public String getStructName(){
        return this.identifier.getValue();
    }
    public int getLine(){
        return this.identifier.getLine();
    }

    public String toString(){
        return "Struct: {"+
                "Identifier: " + identifier.getValue() + ","+
                "declaredVariables: "+variableDeclarations +
                "}";
    }

    public ArrayList<VariableDeclaration> getVariableDeclarations(){
        return this.variableDeclarations;
    }

    @Override
    public Type accept(VisitorType visitor, SymbolTable symbolTable, SymbolTable structTable) throws SemanticException {
        throw new SemanticException("Sould not run");
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
