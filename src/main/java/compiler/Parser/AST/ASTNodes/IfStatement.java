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

public class IfStatement extends ASTNode {
    protected ExpressionStatement ifCondition;
    protected Block ifBlock;
    protected final int if_line;

    public IfStatement(ExpressionStatement ifCondition, Block ifBlock,int if_line){
        this.ifCondition=ifCondition;
        this.ifBlock=ifBlock;
        this.if_line=if_line;
    }

    public IfStatement(int ifLine){
        if_line = ifLine;
        this.ifCondition=null;
        this.ifBlock=null;
    }

    public String toString(){
        return "IfStatement : {" +
                "ifCondition : " + ifCondition + "," +
                "ifBlock : {" + ifBlock + "}}";

    }

    public int getIf_line() {
        return if_line;
    }

    public ExpressionStatement getIfCondition() {
        return ifCondition;
    }

    public Block getIfBlock() {
        return ifBlock;
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
