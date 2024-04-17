package compiler.Parser.AST.ASTNodes;


import compiler.Exceptions.SemanticException.SemanticErrorException;
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

    public Symbol getIdentifier(){
        return this.identifier;
    }

    public Type getType(){
        return this.type;
    }

    public ExpressionStatement getRight_side(){
        return this.right_side;
    }

    public void accept(Visitor visitor, SymbolTable symbolTable,SymbolTable structTable) throws SemanticErrorException {
        visitor.visit(this, symbolTable);
    }


    public String toString(){
        return "Constant: {"+
                "Type: " + type + ","+
                "Identifier :" + identifier.getValue() + ","+
                "Value: "+right_side +
                "}";
    }


}
