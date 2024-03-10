package compiler.Lexer.SpeciefiedSymbol;

import compiler.Lexer.Token;
import compiler.Lexer.Symbol;

public class ArithmeticOperator extends Symbol {
    public ArithmeticOperator(String e, int n_line) {
        super(Token.ArithmeticOperator, e, n_line);
    }

}
