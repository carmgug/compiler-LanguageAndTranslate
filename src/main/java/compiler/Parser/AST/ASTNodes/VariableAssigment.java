package compiler.Parser.AST.ASTNodes;

import compiler.Lexer.Symbol;
import compiler.Parser.AST.ASTNode;
import compiler.Parser.AST.ASTNodes.Expressions.Type;

public class VariableAssigment extends ASTNode {

    protected Symbol identifier;
    protected ExpressionStatement right_side;

    public VariableAssigment(Symbol identifier, ExpressionStatement right_side) {
        this.identifier = identifier;
        this.right_side = right_side;
    }

    public String toString() {
        return "Identifier: " + this.identifier + " =  " + right_side;
    }

}