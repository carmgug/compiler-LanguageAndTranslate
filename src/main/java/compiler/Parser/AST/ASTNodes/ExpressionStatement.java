package compiler.Parser.AST.ASTNodes;

import compiler.Parser.AST.ASTNode;

public class ExpressionStatement extends ASTNode {

    private String descriptor;//boolean String Type

    @Override
    public String toString() {
        return "ExpressionStatement{" +
                "descriptor='" + descriptor + '\'' +
                '}';
    }

}
