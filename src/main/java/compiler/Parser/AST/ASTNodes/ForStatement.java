package compiler.Parser.AST.ASTNodes;

import com.google.common.collect.ForwardingSet;
import compiler.Exceptions.SemanticException.SemanticErrorException;
import compiler.Parser.AST.ASTNode;
import compiler.SemanticAnalysis.SymbolTable;
import compiler.SemanticAnalysis.Visitor.Visitor;

public class ForStatement extends ASTNode {

    protected VariableAssigment start;
    protected ExpressionStatement endCondition;
    protected VariableAssigment update;
    protected Block block;

    public ForStatement(VariableAssigment start,ExpressionStatement endCondition, VariableAssigment update, Block block){
        this.start=start;
        this.endCondition=endCondition;
        this.update=update;
        this.block=block;
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
    public void accept(Visitor visitor, SymbolTable symbolTable) throws SemanticErrorException {
        visitor.visit(this,symbolTable);
    }
}
