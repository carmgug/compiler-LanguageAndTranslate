package compiler.Parser.AST.ASTNodes;

import compiler.Parser.AST.ASTNode;

public class ReturnStatement extends ASTNode {

    private final ExpressionStatement expression;

    public ReturnStatement(){//return ;
        this.expression=null;
    }
    public ReturnStatement(ExpressionStatement expression){//return expression;
        this.expression=expression;
    }

    public String toString(){
        if(expression==null){
            return "Return";
        }
        return "Return: "+expression;
    }
}
