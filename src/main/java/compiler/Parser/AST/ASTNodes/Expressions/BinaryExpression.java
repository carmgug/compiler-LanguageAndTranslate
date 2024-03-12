package compiler.Parser.AST.ASTNodes.Expressions;

import compiler.Lexer.Symbol;
import compiler.Parser.AST.ASTNodes.ExpressionStatement;

public class BinaryExpression extends ExpressionStatement {

    Operator operator;
    ExpressionStatement right;
    ExpressionStatement left;


    public BinaryExpression(ExpressionStatement left, Symbol operator, ExpressionStatement right) {
        this.left=left;
        this.operator = new Operator(operator);
        this.right = right;
    }

    public String toString() {
        return toString(0);
    }

    private String toString(int indent) {
        String indentation = generateIndentation(indent);
        return "BinaryExpression{\n" +
                indentation + "\tleft=" + (left instanceof BinaryExpression ? ((BinaryExpression) left).toString(indent + 1) : left) + "\n" +
                indentation + "\toperator=" + operator + "\n" +
                indentation + "\tright=" + (right instanceof BinaryExpression ? ((BinaryExpression) right).toString(indent + 1) : right) + "\n" +
                indentation + "}";
    }

    private String generateIndentation(int indent) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            sb.append("\t");
        }
        return sb.toString();
    }
}
