package compiler.Parser.AST.ASTNodes.Expressions;

import compiler.CodeGenerator.CodeGenerationVisitor;
import compiler.CodeGenerator.EvaluateVisitor;
import compiler.CodeGenerator.ScopesTable;
import compiler.Exceptions.SemanticException.SemanticException;
import compiler.Parser.AST.ASTNodes.ExpressionStatement;
import compiler.Parser.AST.ASTNodes.Expressions.Types.ArrayType;
import compiler.SemanticAnalysis.SymbolTable.SymbolTable;
import compiler.SemanticAnalysis.Visitor.Visitor;
import compiler.SemanticAnalysis.Visitor.VisitorType;
import org.objectweb.asm.MethodVisitor;

public class StructAccess extends ExpressionStatement {

    private final ExpressionStatement leftPart;
    private final ExpressionStatement rightPart;
    private final int line;

    //Parameters to update during semantic analysis because we need to know, for the codeGeneration
    private Type leftType;
    private Type rightType;

    public Type getLeftType() {
        return leftType;
    }

    public void setLeftType(Type leftType) {
        this.leftType = leftType;
    }

    public Type getRightType() {
        return rightType;
    }

    public void setRightType(Type rightType) {
        if(this.rightPart instanceof ArrayAccess){
            this.rightType = new ArrayType(rightType.getSymbol());
            return;
        }
        this.rightType = rightType;
    }


    public StructAccess(ExpressionStatement leftPart, ExpressionStatement rightPart,int line) {
        this.line= line;
        this.leftPart = leftPart;
        this.rightPart = rightPart;
    }
    public String toString() {
        return
                "StructAccess{" +
                        "leftPart: " + leftPart + ","+
                        "rightPart: " + rightPart +
                "}";
    }

    public int getLine() {
        return line;
    }

    public ExpressionStatement getLeftPart() {
        return leftPart;
    }

    public ExpressionStatement getRightPart() {
        return rightPart;
    }

    @Override
    public void accept(Visitor visitor, SymbolTable symbolTable,SymbolTable structTable) throws SemanticException {
        visitor.visit(this,symbolTable,structTable);
    }

    @Override
    public Type accept(VisitorType visitorType, SymbolTable symbolTable, SymbolTable structTable) throws SemanticException {
        return visitorType.visit(this,symbolTable,structTable);
    }

    @Override
    public void accept(CodeGenerationVisitor codeGenerationVisitor, ScopesTable curr_scope, MethodVisitor mw) {
        throw new RuntimeException("Should not run!");
    }

    @Override
    public void accept(EvaluateVisitor visitor, ScopesTable curr_scope, MethodVisitor mw) {
        visitor.visit(this,curr_scope,mw);
    }
}

