package compiler.Parser.AST.ASTNodes;


import compiler.Lexer.Symbol;
import compiler.Parser.AST.ASTNode;
import compiler.Parser.AST.ASTNodes.Expressions.BinaryExpression;
import compiler.Parser.AST.ASTNodes.Expressions.Type;

public class Constant extends ASTNode {

    private Type type;
    private Symbol identifier;
    private ExpressionStatement right_side;

    public Constant(Type type, Symbol identifier, ExpressionStatement right_side) {
        this.type = type;
        this.identifier = identifier;
        this.right_side = right_side;
    }





    public String toString(){
        return "Constant: {"+
                "Type: " + type + ","+
                "Identifier :" + identifier.getValue() + ","+
                "Value: "+right_side +
                "}";
    }


}
