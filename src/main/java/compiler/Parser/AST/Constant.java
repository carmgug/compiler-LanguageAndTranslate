package compiler.Parser.AST;

import compiler.Lexer.SpeciefiedSymbol.Identifier;
import compiler.Lexer.Symbol;
import compiler.Parser.AST.Expr.Expression;

public class Constant extends ASTNode{
    private Symbol type;
    private Identifier identifier;
    private Expression value;
}
