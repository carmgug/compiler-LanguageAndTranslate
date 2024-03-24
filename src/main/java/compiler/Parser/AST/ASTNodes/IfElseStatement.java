package compiler.Parser.AST.ASTNodes;

import compiler.Parser.AST.ASTNode;

public class IfElseStatement extends IfStatement {

    protected Block else_block;

    public IfElseStatement(ExpressionStatement if_condition,Block if_block,Block else_block){
        super(if_condition,if_block);
        this.else_block=else_block;
    }

    public String toString(){
        return "IfElseStatement : {" +
                "ifCondition:" + ifCondition +
                ", ifBlock : {" + ifBlock + "}"+
                ", elseBlock : {" + else_block + "}"+
                '}';
    }

}
