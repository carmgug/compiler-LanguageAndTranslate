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

public class ArrayInitialization extends ExpressionStatement {

    private final Type type;
    private final ExpressionStatement size;
    private final int line;

    public ArrayInitialization(Type type, ExpressionStatement size,int line) {
        this.type = type;
        this.size = size;
        this.line=line;
    }

    public String toString() {
        return "ArrayInitialization {" +
                    "type: " + type.toString() +","+
                    "size: " + size.toString()+
                "}";
    }

    public Type getType(){return this.type;}

    public ExpressionStatement getSize(){return this.size;}

    public int getLine(){
        return line;
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
        throw new RuntimeException("Should not run!");
    }
}
