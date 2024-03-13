package compiler.Parser;
import compiler.Lexer.*;

import compiler.Parser.AST.ASTNodes.Constant;
import compiler.Parser.AST.ASTNodes.ExpressionStatement;
import compiler.Parser.AST.ASTNodes.Expressions.NegationNodes.ArithmeticNegationNode;
import compiler.Parser.AST.ASTNodes.Expressions.BinaryExpression;
import compiler.Parser.AST.ASTNodes.Expressions.NegationNodes.BooleanNegationNode;
import compiler.Parser.AST.ASTNodes.Expressions.Type;
import compiler.Parser.AST.ASTNodes.Expressions.Types.ArrayStructType;
import compiler.Parser.AST.ASTNodes.Expressions.Types.ArrayType;
import compiler.Parser.AST.ASTNodes.Expressions.Types.BaseType;
import compiler.Parser.AST.ASTNodes.Expressions.Types.StructType;
import compiler.Parser.AST.ASTNodes.Expressions.Value;
import compiler.Parser.AST.ASTNodes.Field;
import compiler.Parser.AST.ASTNodes.Struct;
import compiler.Parser.AST.Program;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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


    public static void main(String[] args) throws IOException {
        String test="final int a= !(a && b || c >= 3);\nfinal float b= 3.0;\nfinal bool c= true;\nfinal string d= \"ciao\";";
        System.out.println(test);
        StringReader stringReader= new StringReader(test);
        Lexer l= new Lexer(stringReader,true);
        Parser p= new Parser(l);
        Program program=p.getAST();
    }

    /*
        @return the Abstract Syntax Tree
     */
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
        while(lookahead.getValue().equals("struct")){
            consume(Token.Keywords);
            Struct curr_stuct=parseStruct();

        }

        return program;
    }


    /*
        @param token: the type of Symbol to consume
        @return the current symbol
     */
    private Symbol consume(Token token) throws IOException {
        Symbol curr_symbol=lookahead;

        if (!lookahead.getType().isEqual(token.name())){
            throw new RuntimeException("Unexpected token: "+lookahead.getValue()+" expected: "+token.name());
        }
        lookahead = lexer.getNextSymbol();
        return curr_symbol;
    }

    private boolean isType(Token token){
        return lookahead.getType().isEqual(token.name());
    }

    private Type parseType() throws IOException {

        switch (lookahead.getType().name()){
            case "BasedType":
                return parseBasedType();
            case "Identifier":
                return parseIdentifierType();
            default:
                throw new RuntimeException("Expected a type but found: "+lookahead.getValue());
        }
    }

    private Type parseIdentifierType() throws IOException {
        Symbol type=consume(Token.Identifier);
        if(isType(Token.SpecialCharacter)){
            if(!lookahead.getValue().equals("[")) throw new RuntimeException("Expected [ but found: "+lookahead.getValue()); //TODO throw an exception
            consume(Token.SpecialCharacter); //Expected [
            if(!lookahead.getValue().equals("]")) throw new RuntimeException("Expected [ but found: "+lookahead.getValue()); //TODO throw an exception
            consume(Token.SpecialCharacter); //Expected ]
            return new ArrayStructType(type);
        }
        return new StructType(type);
    }
    private Type parseBasedType() throws IOException {
        Symbol type=consume(Token.BasedType);
        if(isType(Token.SpecialCharacter)){
            if(!lookahead.getValue().equals("[")) throw new RuntimeException("Expected [ but found: "+lookahead.getValue()); //TODO throw an exception
            consume(Token.SpecialCharacter); //Expected [
            if(!lookahead.getValue().equals("]")) throw new RuntimeException("Expected [ but found: "+lookahead.getValue()); //TODO throw an exception
            consume(Token.SpecialCharacter); //Expected ]
            return new ArrayType(type);
        }
        return new BaseType(type);
    }



    /*
        Parse a constant; a constant is a final variable
     */
    private Constant parseConstant() throws IOException {
        consume(Token.Keywords); //Expected final
        Type type = parseType();
        if(!(type instanceof BaseType)) throw new RuntimeException("The type of a constant must be a base type");
        Symbol identifier = consume(Token.Identifier);
        consume(Token.AssignmentOperator); //Expected identifier
        //Parse the right value of Constant, an Expression
        ExpressionStatement expr=parseExpression();
        consume(Token.SpecialCharacter); //Expected ;
        return new Constant(type,identifier,expr);
    }


    /*
        Parse an expression
        Expression -> AdditiveExpression
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
        while (lookahead.getType().equals(Token.ComparisonOperator)) {
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



    /*
        Parse a Factor
        Factor -> IntNumber| FloatNumber|BooleanVale|String|Identifier| (Expression) | (Negate) - Expression | ! Expression
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
            if(expr instanceof ArithmeticNegationNode){throw new RuntimeException("Due meno meno di seguito non sono consentiti");}//TODO throw an exception
            return new ArithmeticNegationNode(expr);
        } else if(lookahead.getValue().equals("!")){
            consume(Token.LogicalOperator); //Expected !
            //We dont have the same situation of - beacause !!! are allowed so is the negate of the nagate of the nagate of the expression
            ExpressionStatement expr=parseExpression();
            return new BooleanNegationNode(expr);
        }
            else {
            throw new RuntimeException("Non ho trovato quello che mi serviva");
        }
    }









}
