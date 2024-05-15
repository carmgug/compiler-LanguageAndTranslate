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

public class WhileStatement extends ASTNode {

    private final ExpressionStatement exitCondition;

    private final Block block;
    private final int line;

    public WhileStatement(ExpressionStatement exitCondition, Block block, int line){
        this.exitCondition=exitCondition;
        this.block=block;
        this.line=line;
    }


    @Override
    public String toString() {
        return "WhileStatement : {" +
                "exitCondition=" + exitCondition +
                ", block:{" + block +
                "}}";
    }

    public ExpressionStatement getExitCondition() {
        return exitCondition;
    }

    public Block getBlock() {
        return block;
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
        throw new RuntimeException("Should not run!");
    }

    @Override
    public void accept(EvaluateVisitor visitor, ScopesTable curr_scope, MethodVisitor mw) {
        throw new RuntimeException("Should not run!");
    }


    public int getLine() {
        return line;
    }
}
