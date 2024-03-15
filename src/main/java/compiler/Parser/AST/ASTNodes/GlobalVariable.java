package compiler.Parser.AST.ASTNodes;

import compiler.Lexer.Symbol;
import compiler.Parser.AST.ASTNodes.Expressions.Type;

public class GlobalVariable {

    private Type type;

    private Symbol identifier;

    private ExpressionStatement value;

    public GlobalVariable(Type type, Symbol identifier, ExpressionStatement value) {
        this.type = type;
        this.identifier = identifier;
        this.value = value;
    }

    public String toString() {
        return "{Type: " + type + ", Identifier: " + identifier + ", Value: " +"\n"+value + "}";
    }

}
