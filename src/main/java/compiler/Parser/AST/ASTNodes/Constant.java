package compiler.Parser.AST.ASTNodes;

import compiler.Lexer.SpeciefiedSymbol.BaseType;
import compiler.Lexer.SpeciefiedSymbol.Identifier;
import compiler.Lexer.Symbol;

public class Constant {

    private Symbol type;
    private Symbol identifier;
    private ExpressionStatement right_side;

    public Constant(Symbol type, Symbol identifier, ExpressionStatement right_side) {
        this.type = type;
        this.identifier = identifier;
        this.right_side = right_side;
    }

    public String toString() {
        return "{Type: " + type + ", Identifier: " + identifier + ", Right Side: " +"\n"+right_side + "}";
    }
}
