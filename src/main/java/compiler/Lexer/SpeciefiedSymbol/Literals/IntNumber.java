package compiler.Lexer.SpeciefiedSymbol.Literals;

import compiler.Lexer.Token;

public class IntNumber extends Literal{
    public IntNumber( String e, int n_line) {
        super(Token.IntNumber, e, n_line);
    }
}
