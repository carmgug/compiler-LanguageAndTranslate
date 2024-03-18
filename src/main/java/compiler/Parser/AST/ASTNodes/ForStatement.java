package compiler.Parser.AST.ASTNodes;

import com.google.common.collect.ForwardingSet;
import compiler.Parser.AST.ASTNode;

public abstract class ForStatement extends ASTNode {

    protected ExpressionStatement endCondition;
    protected VariableAssigment update;
    protected Block block;

    public ForStatement(ExpressionStatement endCondition, VariableAssigment update, Block block){
        this.endCondition=endCondition;
        this.update=update;
        this.block=block;
    }






}
