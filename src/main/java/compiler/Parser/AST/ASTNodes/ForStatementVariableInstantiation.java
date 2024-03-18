package compiler.Parser.AST.ASTNodes;

public class ForStatementVariableInstantiation extends ForStatement{
    VariableInstantiation index;

    public ForStatementVariableInstantiation(VariableInstantiation index, ExpressionStatement endCondition, VariableAssigment update, Block block) {
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
