package compiler.Lexer.SpeciefiedSymbol;

import compiler.Lexer.Symbol;
import compiler.Lexer.Token;

public class Identifier extends Symbol {
    public Identifier(String e, int n_line) {
        super(Token.Identifier, e, n_line);
    }
}
