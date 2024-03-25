package compiler.Parser.AST.ASTNodes;

import compiler.Lexer.Symbol;
import compiler.Parser.AST.ASTNode;
import compiler.Parser.AST.ASTNodes.Expressions.Type;
import compiler.Parser.AST.ASTNodes.Expressions.VariableReference;

public class VariableInstantiation  extends VariableDeclaration {

    protected ExpressionStatement right_side;

    public VariableInstantiation(Type type, VariableReference variableReference, ExpressionStatement right_side){
        super(type,variableReference);
        this.right_side=right_side;
    }

    public String toString(){
        return "VariableInstantiation: {"+
                "Type: " + type + ","+
                "Identifier: " + variable.getIdentifier() + ","+
                "Right side: " + right_side +
                "}";
    }
}
