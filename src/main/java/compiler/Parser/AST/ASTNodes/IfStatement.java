package compiler.Parser.AST.ASTNodes;

import java.beans.Expression;

public class IfStatement {
    Expression ifCondition;
    Block ifBlock;

    public IfStatement(Expression ifCondition, Block ifBlock,
                       Expression elseCondition, Block elseBlock){
        this.ifCondition=ifCondition;
        this.ifBlock=ifBlock;
    }

    public IfStatement(){
        this.ifCondition=null;
        this.ifBlock=null;
    }

    public IfStatement(Expression ifCondition, Block ifBlock){
        this.ifCondition=ifCondition;
        this.ifBlock=ifBlock;
    }


}
