package compiler.Parser.AST.ASTNodes;

import compiler.Parser.AST.ASTNode;

import java.beans.Expression;

public class IfStatement extends ASTNode {
    protected ExpressionStatement ifCondition;
    protected Block ifBlock;

    public IfStatement(Expression ifCondition, Block ifBlock,
                       Expression elseCondition, Block elseBlock){
        this.ifCondition=ifCondition;
        this.ifBlock=ifBlock;
    }

    public IfStatement(){
        this.ifCondition=null;
        this.ifBlock=null;
    }

    public String toString(){
        return "IfStatement{" +
                "ifCondition=" + ifCondition +
                ", ifBlock=" + ifBlock +
                '}';
    }

    public IfStatement(Expression ifCondition, Block ifBlock){
        this.ifCondition=ifCondition;
        this.ifBlock=ifBlock;
    }


}
