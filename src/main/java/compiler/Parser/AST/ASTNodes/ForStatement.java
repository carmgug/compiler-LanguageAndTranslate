package compiler.Parser.AST.ASTNodes;

import compiler.Exceptions.SemanticException.SemanticException;
import compiler.Parser.AST.ASTNode;
import compiler.Parser.AST.ASTNodes.Expressions.Type;
import compiler.SemanticAnalysis.SymbolTable.SymbolTable;
import compiler.SemanticAnalysis.Visitor.Visitor;
import compiler.SemanticAnalysis.Visitor.VisitorType;

public class ForStatement extends ASTNode {

    protected final VariableAssigment start;
    protected final ExpressionStatement endCondition;
    protected final VariableAssigment update;
    protected final Block block;
    protected final int line;

    public ForStatement(VariableAssigment start,ExpressionStatement endCondition, VariableAssigment update, Block block,int line){
        this.start=start;
        this.endCondition=endCondition;
        this.update=update;
        this.block=block;
        this.line=line;
    }

    public String toString() {
        return "ForStatement{" +
                "start : " + (start==null ? "None" : start) +
                ",endCondition :" + (endCondition==null ? "True" : endCondition) +
                ",update:" + (update==null ? "None" : update) +
                ",block:{" + block +
                "}}";
    }

    public ExpressionStatement getEndCondition() {
        return endCondition;
    }

    public VariableAssigment getStart() {
        return start;
    }

    public Block getBlock() {
        return block;
    }

    public VariableAssigment getUpdate() {
        return update;
    }

    @Override
    public void accept(Visitor visitor, SymbolTable symbolTable,SymbolTable structTable) throws SemanticException {
        visitor.visit(this,symbolTable,structTable);
    }

    @Override
    public Type accept(VisitorType visitor, SymbolTable symbolTable, SymbolTable structTable) throws SemanticException {
        return null;
    }

    public int getLine() {
        return line;
    }
}
