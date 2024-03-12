package compiler.Parser;
import compiler.Lexer.*;

import compiler.Parser.AST.ASTNodes.Constant;
import compiler.Parser.AST.ASTNodes.ExpressionStatement;
import compiler.Parser.AST.ASTNodes.Expressions.BinaryExpression;
import compiler.Parser.AST.ASTNodes.Expressions.NegativeNode;
import compiler.Parser.AST.ASTNodes.Expressions.Value;
import compiler.Parser.AST.Program;

import java.beans.Expression;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Objects;

public class Parser {
    private final Lexer lexer;
    private Symbol lookahead;
    public Parser(Lexer lexer) {
        this.lexer=lexer;
    }

    public Program getAST() throws IOException{
        Program program = new Program();

        lookahead = lexer.getNextSymbol();
        //First we have to parse the constants
        while(lookahead.getValue().equals("final")){
             Constant curr_constant=parseConstant();
             program.addConstant(curr_constant);
             System.out.println(curr_constant);
             lookahead=lexer.getNextSymbol();
        }
        //Then we have to parse the structures

        return program;
    }

    private Symbol consume(Token token) throws IOException {

        if(lookahead.isEOF()){
            throw new RuntimeException("Unexpected EOF");
        }
        if (!lookahead.getType().isEqual(token.name())){
            throw new RuntimeException("Unexpected token: "+lookahead.getValue()+" expected: "+token.name());
        }
        lookahead = lexer.getNextSymbol();
        return lookahead;
    }



    private Constant parseConstant() throws IOException {
        consume(Token.Keywords); //Expected final
        Symbol type = lookahead;
        consume(Token.BasedType); //Expected type
        Symbol identifier = lookahead;
        consume(Token.Identifier); //Expected identifier
        consume(Token.AssignmentOperator); //Expected identifier
        //Parse the right value of Constant, an Expression
        ExpressionStatement expr=parseExpression();
        //if(!lookahead.getValue().equals(";")){
          //  throw new RuntimeException("Non ho trovato il punto e virgola");
        //}
        return new Constant(type,identifier,expr);

    }


    /*
        Expression :
            Term
            Expression Operator Term
            Identifier
            FunctionCall
     */

    private ExpressionStatement parseExpression() throws IOException{
        //Expression
        return parseAdditiveExpression();
    }


    /*
        +,-, Negative Value (-)
     */
    private ExpressionStatement parseAdditiveExpression() throws IOException{
        //Expression

        ExpressionStatement left = parseMultiplyExpression();
        while (lookahead.getValue().equals("+") || lookahead.getValue().equals("-")) {
            Symbol operator = Symbol.copy(lookahead);
            consume(Token.ArithmeticOperator); //Expected Arithmetic Operator
            ExpressionStatement right = parseMultiplyExpression();
            left = new BinaryExpression(left, operator, right);
        }

        return left;
    }


    /*
        *,/,%
     */
    private ExpressionStatement parseMultiplyExpression() throws IOException {
        ExpressionStatement left = parseFactor();
        while (lookahead.getValue().equals("/")|| lookahead.getValue().equals("*") || lookahead.getValue().equals("%")) {
            Symbol operator = Symbol.copy(lookahead);
            consume(Token.ArithmeticOperator); //Expected Arithmetic Operator
            ExpressionStatement right = parseFactor();
            left = new BinaryExpression(left, operator, right);
            lookahead=lexer.getNextSymbol();
        }
        return left;
    }




    private ExpressionStatement parseFactor() throws IOException {
        if (lookahead.getType().isEqual("IntNumber") || lookahead.getType().isEqual("FloatNumber") ||
                lookahead.getType().isEqual("BooleanValue") || lookahead.getType().isEqual("String") ||
                lookahead.getType().isEqual("Identifier")){
            return new Value(lookahead);
        } else if (lookahead.getValue().equals("(")) {
            ExpressionStatement subExpr=parseExpression();
            if(!lookahead.getValue().equals(")")) throw new RuntimeException("Non ho trovato la parentesi chiusa");//TODO throw an exception
            return subExpr;
        } else if(lookahead.getValue().equals("-")){//Negative Expression
            ExpressionStatement expr=parseExpression();
            if(expr instanceof NegativeNode){throw new RuntimeException("Due meno meno di seguito non sono consentiti");}//TODO throw an exception
            return new NegativeNode(expr);
        }

        else {
            throw new RuntimeException("Non ho trovato quello che mi serviva");
        }


    }











}
