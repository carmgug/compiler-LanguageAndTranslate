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

public class GlobalVariable extends ASTNode {

    private Type type;

    private Symbol identifier;

    private ExpressionStatement value;


    public GlobalVariable(Type type, Symbol identifier, ExpressionStatement value) {
        this.type = type;
        this.identifier = identifier;
        this.value = value;
    }

    public String toString() {
        return "GlobalVariable: {"+
                "Type: " + type + ","+
                "Identifier: " + identifier.getValue() + ","+
                "Value: "+value +
                "}";
    }

    public Symbol getIdentifier(){
        return this.identifier;
    }

    public Type getType(){
        return this.type;
    }

    public ExpressionStatement getValue(){
        return this.value;
    }

    public String getNameOfTheVariable(){
        return this.identifier.getValue();
    }
    public int getLine(){
        return this.identifier.getLine();
    }


    @Override
    public void accept(Visitor visitor, SymbolTable symbolTable,SymbolTable structTable) throws SemanticException {
        visitor.visit(this,symbolTable,structTable);
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
