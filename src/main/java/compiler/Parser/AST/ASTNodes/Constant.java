package compiler.Parser.AST.ASTNodes;

import compiler.Lexer.SpeciefiedSymbol.BaseType;
import compiler.Lexer.SpeciefiedSymbol.Identifier;
import compiler.Lexer.Symbol;

public class Constant {

    private Symbol type;
    private Identifier identifier;
    private Integer value;

    public Constant(BaseType type, Identifier identifier, Object o) {
    }
}
