package compiler.Parser.AST.ASTNodes;

import compiler.Lexer.Symbol;
import compiler.Parser.AST.ASTNode;
import compiler.Parser.AST.ASTNodes.Expressions.Type;

public class VariableInstantiation  extends VariableDeclaration {

    protected ExpressionStatement right_side;

    public VariableInstantiation(Type type, Symbol identifier,ExpressionStatement right_side){
        super(type,identifier);
        this.right_side=right_side;
    }

    public String toString(){
        return "VariableInstantiation: {"+
                "Type: " + type + ","+
                "Identifier: " + identifier.getValue() + ","+
                "Right side: " + right_side +
                "}";
    }
}
