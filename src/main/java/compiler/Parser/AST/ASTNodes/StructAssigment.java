package compiler.Parser.AST.ASTNodes;

import compiler.Parser.AST.ASTNode;
import compiler.Parser.AST.ASTNodes.Expressions.StructAccess;

public class StructAssigment extends ASTNode {

    private StructAccess variable;
    private ExpressionStatement right_side;

    public StructAssigment(StructAccess variable, ExpressionStatement right_side) {
        this.variable = variable;
        this.right_side = right_side;
    }
}
