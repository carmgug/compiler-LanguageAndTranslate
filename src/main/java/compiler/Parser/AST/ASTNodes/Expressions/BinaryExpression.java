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
import org.objectweb.asm.MethodVisitor;

public class BinaryExpression extends ExpressionStatement {

    Operator operator;
    ExpressionStatement right;
    ExpressionStatement left;


    //to update during semantic analysis
    //code Generation use this to know what type of operation need to do
    //IADD OR FADD ECC
    private Type typeResult;
    private Type leftType;
    private Type rightType;


    public BinaryExpression(ExpressionStatement left, Symbol operator, ExpressionStatement right) {
        this.left=left;
        this.operator = new Operator(operator);
        this.right = right;
    }

    private String toString(int indent) {
        String indentation = generateIndentation(indent);
        return "BinaryExpression{\n" +
                indentation + "\tleft=" + (left instanceof BinaryExpression ? ((BinaryExpression) left).toString(indent + 1) : left) + "\n" +
                indentation + "\toperator=" + operator + "\n" +
                indentation + "\tright=" + (right instanceof BinaryExpression ? ((BinaryExpression) right).toString(indent + 1) : right) + "\n" +
                indentation + "}";
    }

    public String toString() {
        return "BinaryExpression : { " +
                "operator : " + operator+
                ", right : " + right +
                ", left : " + left +
                '}';
    }

    public ExpressionStatement getRight(){return this.right;}

    public ExpressionStatement getLeft(){return this.left;}

    public Operator getOperator(){return this.operator;}

    @Override
    public void accept(Visitor visitor, SymbolTable symbolTable,SymbolTable structTable) throws SemanticException {
        visitor.visit(this,symbolTable,structTable);
    }
    public Type accept(VisitorType visitorType, SymbolTable symbolTable, SymbolTable structTable) throws SemanticException {
        return visitorType.visit(this, symbolTable,structTable);
    }

    @Override
    public void accept(CodeGenerationVisitor codeGenerationVisitor, ScopesTable curr_scope, MethodVisitor mw) {
        throw new RuntimeException("Should not be called");
    }

    public void accept(EvaluateVisitor visitor, ScopesTable curr_scope, MethodVisitor mw){
        visitor.visit(this, curr_scope,mw);
    }


    public void setResultType(Type typeResult) {
        this.typeResult = typeResult;
    }

    public Type getResultType() {
        return typeResult;
    }

    public void setLeftType(Type leftType) {
        this.leftType = leftType;
    }

    public Type getLeftType() {
        return leftType;
    }

    public void setRightType(Type rightType) {
        this.rightType = rightType;
    }
    public Type getRightType() {
        return rightType;
    }


}
