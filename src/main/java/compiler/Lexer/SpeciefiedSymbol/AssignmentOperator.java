package compiler.Lexer.SpeciefiedSymbol;

import compiler.Lexer.Symbol;
import compiler.Lexer.Token;

public class AssignmentOperator extends Symbol {
    public AssignmentOperator(String e, int n_line) {
        super(Token.AssignmentOperator, e, n_line);
    }
}
