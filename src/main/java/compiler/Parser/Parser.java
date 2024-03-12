package compiler.Parser;
import compiler.Lexer.*;
import compiler.Lexer.SpeciefiedSymbol.BaseType;
import compiler.Lexer.SpeciefiedSymbol.Identifier;
import compiler.Lexer.SpeciefiedSymbol.Keywords.Final;
import compiler.Lexer.SpeciefiedSymbol.Literals.IntNumber;
import compiler.Lexer.SpeciefiedSymbol.SemiColon;
import compiler.Parser.AST.ASTNodes.Constant;
import compiler.Parser.AST.ASTNodes.ExpressionStatement;
import compiler.Parser.AST.ASTNodes.Expressions.BinaryExpression;
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
             lookahead = lexer.getNextSymbol();
        }
        //Then we have to parse the structures

        return program;
    }

    private Symbol consume(Token token) throws IOException {
        lookahead = lexer.getNextSymbol();
        if (lookahead.getType().isEqual(token.name())){
            return lookahead;
        }
        throw new RuntimeException("Non ho trovato quello che mi serviva");
    }



    private Constant parseConstant() throws IOException {
        //Constant
        Symbol type =  consume(Token.BasedType);
        Symbol identifier =  consume(Token.Identifier);
        consume(Token.AssignmentOperator);
        //Ora ci possono essere valori o qualsiasi altra cosa
        //Parse the right value of Constant
        ExpressionStatement expr=parseExpressionStatement();
        System.out.println(expr);
        System.out.println(type);
        System.out.println(identifier);
        return null;

    }


    /*
        Expression :
            Term
            Expression Operator Term
            Identifier
            FunctionCall
     */



    private ExpressionStatement parseExpressionStatement() throws IOException{
        //Expression

        lookahead = lexer.getNextSymbol();
        /*
        Se parentesi di apertura
        parsePar
         */
        System.out.println("Ho preso il simbolo: "+ lookahead+"Sono fuori dal while");
        while(!(lookahead instanceof SemiColon)) {
            ExpressionStatement left = parseFactor();
            lookahead = lexer.getNextSymbol();
            System.out.println("Ho preso il simbolo: "+ lookahead+"Sono dentro il while");
            while (lookahead.isTypeof("ArithmeticOperator")) {
                Symbol operator = Symbol.copy(lookahead);
                lookahead=lexer.getNextSymbol();
                ExpressionStatement right = parseFactor();
                left = new BinaryExpression(left, operator, right);
                lookahead=lexer.getNextSymbol();
                System.out.println("PROSSIMO SIMBOLO"+lookahead);
            }
            return left;
        }
        return null;
    }

    private ExpressionStatement parethesisExpression() throws IOException {
        System.out.println(lookahead+"Sono entrato");
        ExpressionStatement subExpr=parseExpressionStatement();
        System.out.println(lookahead+"Sono uscito");
        return subExpr;
    }

    private ExpressionStatement parseFactor() throws IOException {
        if (lookahead.getType().isEqual("IntNumber") || lookahead.getType().isEqual("FloatNumber") ||
                lookahead.getType().isEqual("BooleanValue") || lookahead.getType().isEqual("String") ||
                lookahead.getType().isEqual("Identifier")){
            return new Value(lookahead);
        } else if (lookahead.getValue().equals("(")) {
            return parethesisExpression();
        }else {
            throw new RuntimeException("Non ho trovato quello che mi serviva");
        }


    }











}
