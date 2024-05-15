package compiler.Parser.AST.ASTNodes;

import compiler.CodeGenerator.CodeGenerationVisitor;
import compiler.CodeGenerator.EvaluateVisitor;
import compiler.CodeGenerator.ScopesTable;
import compiler.Exceptions.SemanticException.SemanticException;
import compiler.Parser.AST.ASTNode;
import compiler.Parser.AST.ASTNodes.Expressions.Type;
import compiler.SemanticAnalysis.SymbolTable.SymbolTable;
import compiler.SemanticAnalysis.Visitor.Visitor;
import compiler.SemanticAnalysis.Visitor.VisitorType;
import org.objectweb.asm.MethodVisitor;

public class VariableAssigment extends ASTNode {

    protected ExpressionStatement variable;
    protected ExpressionStatement right_side;
    protected int line;


    public VariableAssigment(ExpressionStatement variable, ExpressionStatement right_side) {
        this.variable = variable;
        this.right_side = right_side;
    }

    public VariableAssigment(ExpressionStatement variable, ExpressionStatement right_side, int line) {
        this.variable = variable;
        this.right_side = right_side;
        this.line= line;
    }

    public String toString() {
        return "VariableAssigment: {" +
                "Identifier: " + variable + "," +
                "Right side: " + right_side +
                "}";
    }

    public ExpressionStatement getVariable(){return this.variable;}

    public ExpressionStatement getRight_side() {
        return right_side;
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


    public int getLine() {
        return line;
    }
}