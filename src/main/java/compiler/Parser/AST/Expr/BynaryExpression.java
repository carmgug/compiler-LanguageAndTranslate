package compiler.Parser.AST.Expr;

import compiler.Lexer.SpeciefiedSymbol.ArithmeticOperator;

public class BynaryExpression extends Expression{
    private Expression left;
    private Expression right;
    private ArithmeticOperator operator;

public BynaryExpression(Expression l, Expression r, ArithmeticOperator o){
        this.left = l;
        this.right = r;
        this.operator = o;
    }




}
