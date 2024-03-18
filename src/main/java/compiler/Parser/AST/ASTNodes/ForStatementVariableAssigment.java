package compiler.Parser.AST.ASTNodes;

import compiler.Parser.AST.ASTNodes.ForStatement;

public class ForStatementVariableAssigment extends ForStatement {

    VariableAssigment index;

    public ForStatementVariableAssigment(VariableAssigment index,ExpressionStatement endCondition, VariableAssigment update, Block block) {
        super(endCondition, update, block);
        this.index = index;
    }

    public String toString() {
        return "ForStatement{" +
                "index=" + index +
                ", endCondition=" + endCondition +
                ", update=" + update +
                ", block={" + block +
                "}}";
    }
}
