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


    /*
    ComparisonExpression exitCondition;
    VariableAssignment indexUpdate
     */


    /*

    public ForStatement(GlobalVariable iterativeIndex,
                        ComparisonExpression exitCondition,
                        VariableAssigment indexUpdate,
                        Block block){
        this.iterativeIndex=iterativeIndex;
        this.exitCondition=exitCondition;
        this.indexUpdate=indexUpdate;
        this.block=block;
    }

    public ForStatement(){
        this.iterativeIndex=null;
        this.exitCondition=null;
        this.indexUpdate=null;
        this.block=null;

    }
     */






}
