package compiler.Parser.AST.ASTNodes.Expressions;

import compiler.CodeGenerator.CodeGenerationVisitor;
import compiler.CodeGenerator.EvaluateVisitor;
import compiler.CodeGenerator.ScopesTable;
import compiler.Exceptions.SemanticException.SemanticException;
import compiler.Lexer.Symbol;
import compiler.Parser.AST.ASTNodes.ExpressionStatement;
import compiler.SemanticAnalysis.SymbolTable.SymbolTable;
import compiler.SemanticAnalysis.Visitor.Visitor;
import compiler.SemanticAnalysis.Visitor.VisitorType;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

public class Value extends ExpressionStatement {
    Symbol value;

    public Value(Symbol value) {
        this.value = Symbol.copy(value);

    }

    public String toString() {
        return "Value: {"+
                "Type: " + value.getType() + ","+
                "Value: " + value.getValue() +
                "}";
    }

    public Symbol getSymbol() {
        return value;
    }

    public String getValue(){
        return value.getValue();
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

    public void accept(EvaluateVisitor visitor, ScopesTable curr_scope, MethodVisitor mw){
        visitor.visit(this, curr_scope,mw);
    }
}


