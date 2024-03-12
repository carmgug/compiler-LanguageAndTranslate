package compiler.Lexer.SpeciefiedSymbol;

import compiler.Lexer.Symbol;
import compiler.Lexer.Token;

public class SemiColon extends Symbol {
    public SemiColon(int n_line) {
        super(Token.SpecialCharacter, ";", n_line);
    }
}
