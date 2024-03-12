package compiler.Lexer.SpeciefiedSymbol.Literals;

import compiler.Lexer.Token;

public class FloatNumber extends Literal {

    public FloatNumber( String e, int n_line) {
        super(Token.FloatNumber, e, n_line);
    }
}
