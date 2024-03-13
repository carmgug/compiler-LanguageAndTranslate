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

    private static final Logger LOGGER = LogManager.getLogger(Parser.class.getName());
    private final Lexer lexer;
    private Symbol lookahead;
    public Parser(Lexer lexer) {
        this.lexer=lexer;
    }

    public Program getAST() throws IOException{
        Program program = new Program();
        //Take the first token. Lookahead is for predective parsing
        lookahead = lexer.getNextSymbol();
        //First we have to parse the constants
        while(lookahead.getValue().equals("final")){
            Constant curr_constant=parseConstant();
            LOGGER.log(Level.DEBUG,"Constant parsed: "+curr_constant);
            program.addConstant(curr_constant);
        }
        //Then we have to parse the structures

        return program;
    }

    private Symbol consume(Token token) throws IOException {
        Symbol curr_symbol=lookahead;

        if (!lookahead.getType().isEqual(token.name())){
            throw new RuntimeException("Unexpected token: "+lookahead.getValue()+" expected: "+token.name());
        }
        lookahead = lexer.getNextSymbol();
        return curr_symbol;
    }


    /*
        Parse a constant; a constant is a final variable
     */
    private Constant parseConstant() throws IOException {
        consume(Token.Keywords); //Expected final
        Symbol type = consume(Token.BasedType);
        Symbol identifier = consume(Token.Identifier);
        consume(Token.AssignmentOperator); //Expected identifier
        //Parse the right value of Constant, an Expression
        ExpressionStatement expr=parseExpression();
        consume(Token.SpecialCharacter); //Expected ;
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
        return parseAndOrExpression();
    }

    /*
        Parse a AndOrExpression:
            AndOrExpression ->  ComparaisonExpression | AndOrExpression AndOrOperator ComparaisonExpression
     */
    private ExpressionStatement parseAndOrExpression() throws IOException{
        ExpressionStatement left = parseComparisonExpression();
        while (lookahead.getValue().equals("&&") || lookahead.getValue().equals("||")) {
            Symbol operator = Symbol.copy(lookahead);
            consume(Token.LogicalOperator); //Expected Logical Operator
            ExpressionStatement right = parseComparisonExpression();
            left = new BinaryExpression(left, operator, right);
        }
        return left;
    }

    /*
        Parse a ComparisonExpression:
            ComparisonExpression ->  AdditiveExpressione | ComparasionExpression ComparisonOperator AdditiveExpression
     */
    private ExpressionStatement parseComparisonExpression() throws IOException{
        ExpressionStatement left = parseAdditiveExpression();
        while (lookahead.getValue().equals("==") || lookahead.getValue().equals("<") || lookahead.getValue().equals(">") || lookahead.getValue().equals("<=") || lookahead.getValue().equals(">=") || lookahead.getValue().equals("!=")) {
            Symbol operator = Symbol.copy(lookahead);
            consume(Token.ComparisonOperator); //Expected Comparison Operator
            ExpressionStatement right = parseAdditiveExpression();
            left = new BinaryExpression(left, operator, right);
        }
        return left;
    }


    /*
        Parse an AdditiveExpression :
            MultiplicativeExpression | AdditiveExpression AdditiveOperator MultiplicativeExpression

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
        Parse an MultiplicativeExpression
        could be a Factor |
        MultiplicativeExpression MultiplyOperator Factor ->
            Factor MultiplyOperator Factor MultiplyOperator Factor
        +,-, Negative Value (-)
     */

    private ExpressionStatement parseMultiplyExpression() throws IOException {
        ExpressionStatement left = parseFactor();
        while (lookahead.getValue().equals("/")|| lookahead.getValue().equals("*") || lookahead.getValue().equals("%")) {
            Symbol operator = Symbol.copy(lookahead);
            consume(Token.ArithmeticOperator); //Expected Arithmetic Operator
            ExpressionStatement right = parseFactor();
            left = new BinaryExpression(left, operator, right);
        }
        return left;
    }



    /*
        Parse a Factor
        Factor -> IntNumber| FloatNumber|BooleanVale|String|Identifier| (Expression) | (Negate) -Expression
     */
    private ExpressionStatement parseFactor() throws IOException {

        if(lookahead.isTypeof("IntNumber")){
            return new Value(consume(Token.IntNumber));
        } else if (lookahead.isTypeof("FloatNumber")) {
            return new Value(consume(Token.FloatNumber));
        } else if (lookahead.isTypeof("BooleanValue")) {
            return new Value(consume(Token.BooleanValue));
        }else if (lookahead.isTypeof("String")) {
            return new Value(consume(Token.String));
        }else if (lookahead.isTypeof("Identifier")){
            return new Value(consume(Token.Identifier));
        } else if (lookahead.getValue().equals("(")) {
            consume(Token.SpecialCharacter); //Expected ( as if statement
            ExpressionStatement subExpr=parseExpression();
            if(!lookahead.getValue().equals(")")) throw new RuntimeException("Non ho trovato la parentesi chiusa");//TODO throw an exception
            consume(Token.SpecialCharacter); //Expected )
            return subExpr;
        } else if(lookahead.getValue().equals("-")){//Negative Expression
            consume(Token.ArithmeticOperator); //Expected -
            ExpressionStatement expr=parseExpression();
            if(expr instanceof NegativeNode){throw new RuntimeException("Due meno meno di seguito non sono consentiti");}//TODO throw an exception
            return new NegativeNode(expr);
        } else {
            throw new RuntimeException("Non ho trovato quello che mi serviva");
        }
    }











}
