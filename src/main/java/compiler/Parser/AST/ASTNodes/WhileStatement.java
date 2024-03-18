package compiler.Parser.AST.ASTNodes;

import compiler.Parser.AST.ASTNode;

public class WhileStatement extends ASTNode {



    private ExpressionStatement exitCondition;

    private Block block;

    public WhileStatement(ExpressionStatement exitCondition, Block block){
        this.exitCondition=exitCondition;
        this.block=block;
    }


    @Override
    public String toString() {
        return "WhileStatement{" +
                "exitCondition=" + exitCondition +
                ", block={" + block +
                "}}";
    }
}
