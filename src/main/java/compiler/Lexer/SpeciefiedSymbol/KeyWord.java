package compiler.Lexer.SpeciefiedSymbol;

import compiler.Lexer.Symbol;
import compiler.Lexer.Token;

public class KeyWord extends Symbol {
    public KeyWord(Token type, String e, int n_line) {
        super(type, e, n_line);
        if(type != Token.Keywords)
            throw new RuntimeException("KeyWord created but not with KeyWord type.");
    }
}
