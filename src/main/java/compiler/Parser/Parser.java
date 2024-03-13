package compiler.Parser;
import compiler.Lexer.*;

import compiler.Parser.AST.ASTNodes.Constant;
import compiler.Parser.AST.ASTNodes.ExpressionStatement;
import compiler.Parser.AST.ASTNodes.Expressions.BinaryExpression;
import compiler.Parser.AST.ASTNodes.Expressions.NegativeNode;
import compiler.Parser.AST.ASTNodes.Expressions.Value;
import compiler.Parser.AST.ASTNodes.Field;
import compiler.Parser.AST.ASTNodes.Struct;
import compiler.Parser.AST.Program;

import java.beans.Expression;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
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
             lookahead=lexer.getNextSymbol();
        }
        while(lookahead.getValue().equals("struct")){
            consume(Token.Keywords);
            Struct curr_stuct=parseStruct();

        }

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
        return parseAdditiveExpression();
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

    private Struct parseStruct() throws IOException {
        Symbol identifier=consume(Token.Identifier);
        consume(Token.SpecialCharacter); //{ expected
        ArrayList<Field> fields=parseFields();
        return new Struct(identifier,fields);
    }

    private ArrayList<Field> parseFields() throws IOException {
        ArrayList<Field> ret=new ArrayList<>();
        while(!lookahead.getValue().equals("}")){
            Symbol type=consume(Token.BasedType);
            Symbol identifier=consume(Token.Identifier);
            consume(Token.SpecialCharacter); //; expected
            ret.add(new Field(type,identifier));
        }
        return ret;
    }




    private ExpressionStatement parseFactor() throws IOException {

        switch (lookahead.getType().name()) {
            case "IntNumber":
                return new Value(consume(Token.IntNumber));
            case "FloatNumber":
                return new Value(consume(Token.FloatNumber));
            case "BooleanValue":
                return new Value(consume(Token.BooleanValue));
            case "String":
                return new Value(consume(Token.String));
            case "Identifier":
                return new Value(consume(Token.Identifier));
            default:
                // Handle other cases or throw an exception
                break;
        }
        if (lookahead.getValue().equals("(")) {
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
        }

        else {
            throw new RuntimeException("Non ho trovato quello che mi serviva");
        }


    }











}
