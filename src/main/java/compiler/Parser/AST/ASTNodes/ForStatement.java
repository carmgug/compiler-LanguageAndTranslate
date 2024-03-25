package compiler.Parser.AST.ASTNodes;

import com.google.common.collect.ForwardingSet;
import compiler.Parser.AST.ASTNode;

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






}
