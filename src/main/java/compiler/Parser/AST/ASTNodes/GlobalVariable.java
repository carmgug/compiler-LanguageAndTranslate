package compiler.Parser.AST.ASTNodes;

import compiler.Lexer.Symbol;
import compiler.Parser.AST.ASTNode;
import compiler.Parser.AST.ASTNodes.Expressions.Type;

public class GlobalVariable extends ASTNode {

    private Type type;

    private Symbol identifier;

    private ExpressionStatement value;

    public GlobalVariable(Type type, Symbol identifier, ExpressionStatement value) {
        this.type = type;
        this.identifier = identifier;
        this.value = value;
    }

    public String toString() {
        return "GlobalVariable: {"+
                "Type: " + type + ","+
                "Identifier: " + identifier.getValue() + ","+
                "Value: "+value +
                "}";
    }


}
