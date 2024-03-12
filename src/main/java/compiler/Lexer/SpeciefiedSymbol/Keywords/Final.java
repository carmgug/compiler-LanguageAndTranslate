package compiler.Lexer.SpeciefiedSymbol.Keywords;

import compiler.Lexer.SpeciefiedSymbol.KeyWord;
import compiler.Lexer.Token;

public class Final extends KeyWord {
    public Final(String e, int n_line) {
        super(Token.BasedType, e, n_line);
        if(!e.equals("final"))
            throw new RuntimeException("Constant created but not with final.");

    }
}
