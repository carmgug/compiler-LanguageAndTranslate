package compiler.Parser;
import Utility.Utility;
import compiler.Exceptions.ParserExceptions.ParserException;
import compiler.Lexer.*;

import compiler.Parser.AST.ASTNode;
import compiler.Parser.AST.ASTNodes.*;
import compiler.Parser.AST.ASTNodes.Constant;
import compiler.Parser.AST.ASTNodes.ExpressionStatement;
import compiler.Parser.AST.ASTNodes.Expressions.*;
import compiler.Parser.AST.ASTNodes.Expressions.NegationNodes.ArithmeticNegationNode;
import compiler.Parser.AST.ASTNodes.Expressions.NegationNodes.BooleanNegationNode;
import compiler.Parser.AST.ASTNodes.Expressions.Types.*;
import compiler.Parser.AST.ASTNodes.Expressions.Value;
import compiler.Parser.AST.ASTNodes.VariableDeclaration;
import compiler.Parser.AST.ASTNodes.Struct;
import compiler.Parser.AST.Program;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class Parser {

    private static final Logger LOGGER = LogManager.getLogger(Parser.class.getName());
    private final Lexer lexer;
    private Symbol lookahead;
    private boolean debugParser=false;
    public Parser(Lexer lexer) {
        this.lexer=lexer;
    }

    public Parser(Reader r, boolean debugLexer,boolean debugParser) {
        this.lexer = new Lexer(r,debugLexer);
        this.debugParser=debugParser;
    }

    public static void main(String[] args) throws IOException, ParserException {
        String test="final bool isEmpty = isTrue(isTrue()[4.getARandomNumber().ciao[4]]);\nfinal int a_abc_123_ =  3;\n" +
                "final int a_abc_123_ = 3;\n" +
                "final int[] a_abc_123_ = {1,2,3,4,5,6,7,8,9,10};\n" +
                "final float j = 3.256*5.0;\n" +
                "final int k = i*3;\n" +
                "final string message = \"Hello\\n\";\n" +
                "final bool isEmpty = isTrue(a[getNumberOfIncrement()[ciao].x]);\n" +
                "final int x=3;\nfinal int[] x=\"ciao\"+3+4+carmelo.gugliotta.x+x\n;" +
                "struct Point {\n" +
                "\tint x;\n" +
                "\tint y;\n" +
                "}\n" +
                "\n" +
                "struct Person {\n" +
                "\tstring name;\n" +
                "\tPoint location;\n" +
                "\tint[] history;}\n" +
                "\tperson age=Person(\"carmelo\",\"gugliotta\",24).age;\n" +
                "int x=3;\n"+
                "Person p=age.ciao.3;\n"+
                "def void ciao(){for(i[3].c[4]=4,i<3,i[3].c[3]++){return a*b;} return a*b;}\n"+
                "int x=3;\n"+
                "def int[] getArrayFromString(String s){int[] ris=int[len(s)];for(i=0,i<len(s),i++){ris[i]=s[i];}return ris;x[3].ciao=3;}\n" +
                "int[] x=getArrayFromString(\"ciao\");\n";

        String test2="def void ciao(){" +
                "for(,,i++){p.a.b.c = 3; i=5; bool i = 3<=5 ;}" +
                "}";

        String test5="bool flag= i<=3;";

        String test3=""+
                "struct Person {\n" +
                "\tbool p;\n" +
                "\tint s;}\n"+
                "int b = a(3);"+
                "Person p1 = Person(true,2);"+
                "def void metod(){ int i; return ciao;}";
        StringReader stringReader= new StringReader(test5);
        Parser p= new Parser(stringReader,false,true);
        Program program=p.getAST();
        Iterator<ASTNode> it= program.iterator();
        while(it.hasNext()){
            ASTNode node=it.next();
            System.out.println(node);
        }

    }

    /*
        @return the Abstract Syntax Tree
     */
    public Program getAST2() throws IOException, ParserException {
        Program program = new Program();
        //Take the first token. Lookahead is for predective parsing
        lookahead = lexer.getNextSymbol();
        //First we have to parse the constants
        while(isSymbolOfType(Token.Final)){
            Constant curr_constant=parseConstant();
            if(debugParser) LOGGER.log(Level.DEBUG,"Constant parsed:\n"
                    +Utility.indentedString(curr_constant.toString()));
            program.add(curr_constant);
        }

        while(isSymbolOfType(Token.Struct)){
            consume(Token.Struct); //struct consumed
            Struct curr_stuct=parseStruct();
            if(debugParser) LOGGER.log(Level.DEBUG,"Struct parsed:\n"
                    +Utility.indentedString(curr_stuct.toString()));
            program.add(curr_stuct);
        }

        while(isSymbolOfType(Token.BasedType) || isSymbolOfType(Token.Identifier) || isSymbolOfType(Token.Def)){

            if(isSymbolOfType(Token.BasedType) || isSymbolOfType(Token.Identifier)){ //Parse Global Variable
                GlobalVariable curr_global_variable= parseGlobalVariable();
                if(debugParser) LOGGER.log(Level.DEBUG,"Global Variable parsed:\n"+
                        Utility.indentedString(curr_global_variable.toString()));
                program.add(curr_global_variable);
            } else if(isSymbolOfType(Token.Def)){ //Parse Procedure
                consume(Token.Def); //def consumed
                Procedure curr_procedure=parseProcedure();
                if(debugParser) LOGGER.log(Level.DEBUG,"Procedure parsed :\n"+
                        Utility.indentedString(curr_procedure.toString()));
                program.add(curr_procedure);
            }
        }
        if(!isSymbolOfType(Token.EOF)) throw new ParserException("Unexpected token: "+lookahead.getType()+" expected: "+Token.EOF);
        if(debugParser) LOGGER.log(Level.DEBUG,"Program Parsed:\n"+program);
        return program;
    }

    /*
        @return the Abstract Syntax Tree
     */
    public Program getAST() throws IOException, ParserException {
        Program program = new Program();
        //Take the first token. Lookahead is for predective parsing
        lookahead = lexer.getNextSymbol();
        while(true) {
            if (isSymbolOfType(Token.Final)) {
                Constant curr_constant = parseConstant();
                if (debugParser) LOGGER.log(Level.DEBUG, "Constant parsed:\n"
                        + Utility.indentedString(curr_constant.toString()));
                program.add(curr_constant);
            }

            if (isSymbolOfType(Token.Struct)) {
                consume(Token.Struct); //struct consumed
                Struct curr_stuct = parseStruct();
                if (debugParser) LOGGER.log(Level.DEBUG, "Struct parsed:\n"
                        + Utility.indentedString(curr_stuct.toString()));
                program.add(curr_stuct);
            }


            if (isSymbolOfType(Token.BasedType) || isSymbolOfType(Token.Identifier)) { //Parse Global Variable
                GlobalVariable curr_global_variable = parseGlobalVariable();
                if (debugParser) LOGGER.log(Level.DEBUG, "Global Variable parsed:\n" +
                        Utility.indentedString(curr_global_variable.toString()));
                program.add(curr_global_variable);
            }

            if (isSymbolOfType(Token.Def)) { //Parse Procedure
                consume(Token.Def); //def consumed
                Procedure curr_procedure = parseProcedure();
                if (debugParser) LOGGER.log(Level.DEBUG, "Procedure parsed :\n" +
                        Utility.indentedString(curr_procedure.toString()));
                program.add(curr_procedure);
            }

            if (isSymbolOfType(Token.EOF)) break;
        }
        //if(!isSymbolOfType(Token.EOF)) throw new ParserException("Unexpected token: "+lookahead.getType()+" expected: "+Token.EOF);
        if(debugParser) LOGGER.log(Level.DEBUG,"Program Parsed:\n"+program);
        return program;
    }
    /*
        @param token: the type of Symbol to check
        @return true if the current symbol is of the type token
     */
    private boolean isSymbolOfType(Token token){
        if(token.equals(Token.BasedType)){
            if ((lookahead.getType().equals(Token.IntType) ||
                    lookahead.getType().equals(Token.FloatType) ||
                    lookahead.getType().equals(Token.BoolType) ||
                    lookahead.getType().equals(Token.StringType) ) ){
               return true;
            }
            return false;
        }
        else {
            //We dont have a baseType so the lookahead must have the same token of token to be consumed
            return lookahead.getType().equals(token);
        }
    }
    /*
        @param token: the type of Symbol to consume
        @return the current symbol
     */
    private Symbol consume(Token token) throws ParserException, IOException{
        Symbol curr_symbol=lookahead;


        if(token.equals(Token.BasedType)){
            //If we need to consume a based type and we dont have a one of this type then error
            if (!(lookahead.getType().equals(Token.IntType) ||
                    lookahead.getType().equals(Token.FloatType) ||
                    lookahead.getType().equals(Token.BoolType) ||
                    lookahead.getType().equals(Token.StringType) )){
                throw new ParserException("Unexpected token: "+lookahead.getValue()+" expected on this: ["+Token.IntType+","+
                        Token.FloatType+","+Token.BoolType+","+Token.StringType+"]");
            }
        }
        else {
            //We dont have a baseType so the lookahead must have the same token of token to be consumed
            if (!lookahead.getType().equals(token)){
                throw new ParserException("Unexpected token: "+lookahead.getValue()+" expected: "+token+" at line "+lookahead.getLine());
            }
        }

        lookahead = lexer.getNextSymbol();
        return curr_symbol;

    }
    /*
        Type -> BasedType | IdentifierType | Void
     */
    private Type parseType() throws IOException, ParserException {

        switch (lookahead.getType()){
            case IntType:
            case FloatType:
            case BoolType:
            case StringType:
                return parseBasedType();
            case Identifier:
                return parseIdentifierType();
            case Void:
                Symbol s=consume(Token.Void);
                return new VoidType(s);
            default:
                throw new ParserException("Expected a type but found: "+lookahead.getValue());
        }
    }


    /*
        IdentifierType -> StructType
        IdentifierType[] -> ArrayStructType
     */
    private Type parseIdentifierType() throws IOException,ParserException {
        Symbol type=consume(Token.Identifier);
        if(isSymbolOfType(Token.OpeningSquareBracket)){
            consume(Token.OpeningSquareBracket); //Expected [
            consume(Token.ClosingSquareBracket); //Expected ]
            return new ArrayStructType(type);
        }
        return new StructType(type);
    }

    /*
        BasedType -> BaseType
        BasedType[] -> ArrayType
     */
    private Type parseBasedType() throws IOException, ParserException {
        Symbol type=consume(Token.BasedType);
        if(isSymbolOfType(Token.OpeningSquareBracket)){
            consume(Token.OpeningSquareBracket); //Expected [
            consume(Token.ClosingSquareBracket); //Expected ]
            return new ArrayType(type);
        }
        return new BaseType(type);
    }
    /*
        Parse a constant; a constant is a final variable
        Constant -> final Type Identifier = Expression;
     */
    private Constant parseConstant() throws IOException, ParserException {
        consume(Token.Final); //Expected final
        Type type = parseType();
        if(!(type instanceof BaseType)) throw new ParserException("The type of a constant must be a base type");
        Symbol identifier = consume(Token.Identifier);
        consume(Token.AssignmentOperator); //Expected identifier
        //Parse the right value of Constant, an Expression
        ExpressionStatement expr=parseExpression();
        consume(Token.Semicolon); //Expected ;
        return new Constant(type,identifier,expr);
    }

    /*
        Parse a struct; a struct is a collection of fields
        Struct -> struct Identifier { VariableDeclarationInStruct }
     */

    private Struct parseStruct() throws IOException, ParserException {
        Symbol identifier=consume(Token.Identifier);
        consume(Token.OpeningCurlyBrace); //{ expected
        ArrayList<VariableDeclaration> variableDeclarations =parseVariableDeclarationInStruct();
        consume(Token.ClosingCurlyBrace); //} expected
        return new Struct(identifier, variableDeclarations);
    }

    /*
        Parse a list of fields; a field is a variableDeclaration;
        VariableDeclarationInStruct -> Type Identifier ; | VariableDeclarationInStruct | ε
    */

    private ArrayList<VariableDeclaration> parseVariableDeclarationInStruct() throws IOException, ParserException {
        ArrayList<VariableDeclaration> ret=new ArrayList<>();
        while(!isSymbolOfType(Token.ClosingCurlyBrace)){
            Type type=parseType();
            Symbol identifier=consume(Token.Identifier);
            consume(Token.Semicolon); // ; expected
            ret.add(new VariableDeclaration(type,new VariableReference(identifier)));
        }
        return ret;
    }

    /*
        Parse an expression
        Expression -> parseAndOrExpression
     */
    private ExpressionStatement parseExpression() throws IOException, ParserException{
        return parseAndOrExpression();
    }

    /*
        Parse a AndOrExpression:
            AndOrExpression ->  ComparaisonExpression | AndOrExpression AndOrOperator ComparaisonExpression
     */
    private ExpressionStatement parseAndOrExpression() throws IOException, ParserException {
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

    private ExpressionStatement parseComparisonExpression() throws IOException, ParserException {
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
    private ExpressionStatement parseAdditiveExpression() throws IOException, ParserException {
        //Expression

        ExpressionStatement left = parseMultiplyExpression();
        while (isSymbolOfType(Token.AdditiveOperator)) {
            Symbol operator = Symbol.copy(lookahead);
            consume(Token.AdditiveOperator); //Expected Additive Operator
            ExpressionStatement right = parseMultiplyExpression();
            left = new BinaryExpression(left, operator, right);
        }

        return left;
    }

    /*
        Parse an MultiplicativeExpression
        could be a ArrayAccess |
        MultiplicativeExpression MultiplyOperator ArrayAccess ->
            ArrayAccess MultiplyOperator ArrayAccess MultiplyOperator ArrayAccess
        +,-, Negative Value (-)
     */
    private ExpressionStatement parseMultiplyExpression() throws IOException, ParserException {
        ExpressionStatement left = parseArrayAccessOrStructAccess();
        while (isSymbolOfType(Token.MultiplicativeOperator)) {
            Symbol operator = Symbol.copy(lookahead);
            consume(Token.MultiplicativeOperator); //Expected Multiplicative Operator
            ExpressionStatement right = parseArrayAccessOrStructAccess();
            left = new BinaryExpression(left, operator, right);

        }
        return left;
    }
    /*
        Parse a StructAccess
        StructAccess -> Factor | StructAccess . Factor | ArrayAccess . Factor
        ArrayAccess -> StructAccess [ Expression ]

        StructAccess -> Factor StructAccess' | StructAccess [ Expression ] . StructAccess StructAccess'
        StructAccess' -> . Factor StructAccess' | ε
    */
    public ExpressionStatement parseArrayAccessOrStructAccess() throws IOException, ParserException {
        ExpressionStatement leftPart=parseFactor();
        //maybe can be an ArrayAccess, a StructAccess or Otherwise is a simple Factor

        while (isSymbolOfType(Token.Dot) || isSymbolOfType(Token.OpeningSquareBracket)) {
            if (lookahead.getValue().equals(".")) {
                consume(Token.Dot); //Expected .
                ExpressionStatement rightPart = parseArrayAccessOrStructAccess();
                leftPart = new StructAccess(leftPart, rightPart);
            } else if (isSymbolOfType(Token.OpeningSquareBracket)) {
                consume(Token.OpeningSquareBracket); //Expected [
                ExpressionStatement index = parseExpression();
                consume(Token.ClosingSquareBracket); //Expected ]
                leftPart = new ArrayAccess(leftPart, index);
            }
        }
        return leftPart;
    }

    /*
        Parse an ArrayAccess
        ArrayAccess -> StructAccess [ Expression ]
     */
    private GlobalVariable parseGlobalVariable() throws IOException, ParserException {
        Type type=parseType();
        Symbol identifier=consume(Token.Identifier);
        consume(Token.AssignmentOperator); // = expected
        ExpressionStatement expr=parseExpression();
        consume(Token.Semicolon); // ; expected
        return new GlobalVariable(type,identifier,expr);
    }

    /*
        Procedure -> Type Identifier ( VariableDeclarationInProcedure ) { Block }
     */
    private Procedure parseProcedure() throws IOException, ParserException {
        Type returnType=parseType();
        Symbol procedure_name=consume(Token.Identifier);
        consume(Token.OpeningParenthesis); // ( expexted
        ArrayList<VariableDeclaration> parameters_of_the_procedure= parseVariableDeclarationInProcedure();
        consume(Token.ClosingParenthesis); // ) expexted
        consume(Token.OpeningCurlyBrace); // { expexted
        Block block= parseBlock();
        consume(Token.ClosingCurlyBrace); // } expexted
        return new Procedure(returnType,procedure_name,parameters_of_the_procedure,block);
    }

    /*
        Parse the parameters passed to a procedure
        VariableDeclarationInProcedure -> Type Identifier | Type Identifier, VariableDeclarationInProcedure | ε
     */
    private ArrayList<VariableDeclaration> parseVariableDeclarationInProcedure() throws IOException, ParserException {
        ArrayList<VariableDeclaration> ret=new ArrayList<>();
        if(!isSymbolOfType(Token.ClosingParenthesis)){
            Type type=parseType();
            Symbol identifier=consume(Token.Identifier);
            ret.add(new VariableDeclaration(type,new VariableReference(identifier)));
            while(isSymbolOfType(Token.Comma)) {
                consume(Token.Comma); // , expected
                type=parseType();
                identifier=consume(Token.Identifier);
                ret.add(new VariableDeclaration(type,new VariableReference(identifier)));
            }
        }
        return ret;
    }

    /*
        Parse a Variable Declaration
        VariableDeclaration -> Type Identifier ; | VariableInstantiation ;
     */

    private VariableDeclaration parseVariableDeclaration() throws IOException, ParserException {
        Type type = parseType();
        Symbol identifier = consume(Token.Identifier);
        if (lookahead.getValue().equals("=")) {
            //Variable Instantiation
            VariableInstantiation variableInstantiation = parseVariableInstantiation(type, identifier);
            consume(Token.Semicolon); //; expected
            return variableInstantiation;
        }
        //Variable Declaration
        consume(Token.Semicolon); //; expected
        return new VariableDeclaration(type, new VariableReference(identifier));
    }

    /*
        Parse a Variable Instantiation
        VariableInstantiation -> Type Identifier = Expression;
     */
    private VariableInstantiation parseVariableInstantiation(Type type,Symbol identifier) throws IOException, ParserException {
        consume(Token.AssignmentOperator);
        ExpressionStatement exp = parseExpression();
        return new VariableInstantiation(type,new VariableReference(identifier), exp);
    }

    /*
        Parse a Variable Assigment
        VariableAssigment -> IdentifierReference = Expression; | IdentifierReference IncrementOperator;
        IdentifierReference -> VariableReference | VariableReference . IdentifierReference | VariableReference [ Expression ]
        Note that a Variable . IdentifierReference is a StructAccess
        Note that a Variable [ Expression ] is a ArrayAccess
        And so we can have x.y.z = Expression or x[3].y.z=Expression

     */

    private VariableAssigment parseVariableAssigment(Symbol identifier) throws IOException, ParserException {
        ExpressionStatement leftPart = new VariableReference(identifier);
        leftPart = parseLeftPartOfVariableAssigment(leftPart);

        if(isSymbolOfType(Token.IncrementOperator)){
            int n_line=lookahead.getLine();
            consume(Token.IncrementOperator);
            return new VariableAssigment(leftPart,new BinaryExpression(
                    leftPart,
                    new Symbol(Token.AdditiveOperator,"+", n_line),
                    new Value(new Symbol(Token.IntNumber,"1",n_line))));
        }
        consume(Token.AssignmentOperator);
        ExpressionStatement exp = parseExpression();
        return new  VariableAssigment(leftPart, exp);
    }

    /*
        Note that the left part of the assigment can be a struct access, an array access or a simple VariableReference
        that is a simple identifier of a variable declared in the scope


        @param leftPart: the left part of the assigment
        @return the left part of the assigment
        @throws ParserException if the left part of the assigment is not well formed
        @throws IOException if there is an error in the lexer

     */
    private ExpressionStatement parseLeftPartOfVariableAssigment(ExpressionStatement leftPart) throws ParserException, IOException {
        while (isSymbolOfType(Token.Dot) || isSymbolOfType(Token.OpeningSquareBracket)) {
            if (lookahead.getValue().equals(".")) {
                consume(Token.Dot); //Expected .
                ExpressionStatement rightPart = parseLeftPartOfVariableAssigment(new VariableReference(consume(Token.Identifier)));
                leftPart = new StructAccess(leftPart, rightPart);
            } else if (isSymbolOfType(Token.OpeningSquareBracket)) {
                consume(Token.OpeningSquareBracket); //Expected [
                ExpressionStatement index = parseExpression();
                consume(Token.ClosingSquareBracket); //Expected ]
                leftPart = new ArrayAccess(leftPart, index);
            }

        }
        return leftPart;
    }

    /*
        Parse a For statement
        ForStatement -> for (VariableAssigment | epsilon, Expression | epsilon, VariableAssigment | epsilon) {Block}
    */

    private ForStatement parseForStatement() throws IOException, ParserException {
        consume(Token.For); //for
        consume(Token.OpeningParenthesis); //( expected
        VariableAssigment start = null;
        if(isSymbolOfType(Token.Comma)){
            consume(Token.Comma); //, expected
        }else{
            Symbol identifier = consume(Token.Identifier);
            start=parseVariableAssigment(identifier);
            consume(Token.Comma); //, expected
        }
        ExpressionStatement condition;
        if(isSymbolOfType(Token.Comma)){
            condition= null;
            consume(Token.Comma); //, expected
        }else{
            condition=parseExpression();
            consume(Token.Comma); //, expected
        }
        VariableAssigment update;
        if(isSymbolOfType(Token.ClosingParenthesis)){
            update=null;
        }else{
            update=parseVariableAssigment(consume(Token.Identifier));
        }
        consume(Token.ClosingParenthesis); //) expected
        consume(Token.OpeningCurlyBrace); // { expected
        Block block = parseBlock();
        consume(Token.ClosingCurlyBrace); // } expected
        return new ForStatement(start, condition, update, block);
    }

    /*
        Parse an While statemant
        WhileStatement -> while (Expression) Block
     */
    private WhileStatement parseWhileStatement() throws IOException, ParserException {
        consume(Token.While); //while
        consume(Token.OpeningParenthesis); //( expexted
        ExpressionStatement condition=parseExpression();
        consume(Token.ClosingParenthesis); //) expexted
        consume(Token.OpeningCurlyBrace); // { expexted
        Block block = parseBlock();
        consume(Token.ClosingCurlyBrace); // } expexted
        return new WhileStatement(condition,block);
    }
    /*
        Parse an if statement
        IfStatement -> if (Expression) Block | if (Expression) Block else Block
     */
    private IfStatement parseIfStatement() throws IOException, ParserException {
        consume(Token.If); //if
        consume(Token.OpeningParenthesis); //( expected
        ExpressionStatement if_condition=parseExpression();
        consume(Token.ClosingParenthesis); //) expected
        consume(Token.OpeningCurlyBrace); // { expected
        Block if_block = parseBlock();
        consume(Token.ClosingCurlyBrace); // } expected
        if(lookahead.getValue().equals("else")){
            consume(Token.Else); //else expected
            consume(Token.OpeningCurlyBrace); // { expected
            Block else_block = parseBlock();
            consume(Token.ClosingCurlyBrace); // } expected
            return new IfElseStatement(if_condition,if_block,else_block);
        }
        return new IfStatement(if_condition,if_block);
    }
    /*
        Parse a Return statement
        ReturnStatement -> return Expression; | return;
     */
    private ReturnStatement parseReturnStatement() throws IOException, ParserException {
        consume(Token.Return); //return
        if (isSymbolOfType(Token.Semicolon)) { //return;
            consume(Token.Semicolon); //; expected
            return new ReturnStatement();
        }
        ExpressionStatement expr = parseExpression();
        consume(Token.Semicolon); //; expected
        return new ReturnStatement(expr);
    }

    /*
        Parse a Block
        Block -> {Statements; }
        Statements -> Statement | Statement Statements
        Statement -> VariableDeclaration; | VariableAssigment; | FunctionCall; | ForStatement | WhileStatement | IfStatement | ReturnStatement
     */
    private Block parseBlock() throws IOException, ParserException {

        ArrayList<ASTNode> statements_of_theblock=new ArrayList<>();

        while(!isSymbolOfType(Token.ClosingCurlyBrace) && !isSymbolOfType(Token.EOF)) { //waiting for the }
            if (isSymbolOfType(Token.BasedType)) {
                //We can have Variable Instantiation or Variable Declaration
                //Variable Instantiation e Variable Declarion BaseType
                VariableDeclaration curr_variable=parseVariableDeclaration();
                statements_of_theblock.add(curr_variable);
            } else if (isSymbolOfType(Token.Identifier)) {
                // We can have Variable Instantiation or Variable Declaration or We can have Function Call or Variable Assignment
                Symbol curr_identifier = consume(Token.Identifier); //Expected
                if (isSymbolOfType(Token.OpeningParenthesis)) { //FunctionCall
                    //FunctionCall
                    FunctionCall curr_functionCall = parseFunctionCall(curr_identifier);
                    consume(Token.Semicolon); //; expected
                    statements_of_theblock.add(curr_functionCall);
                }
                else if(isSymbolOfType(Token.Identifier)) {//Variable Instantiation or Variable Declaration
                    Type type = new StructType(curr_identifier);
                    Symbol identifier = consume(Token.Identifier);
                    if (isSymbolOfType(Token.AssignmentOperator)) { //Variable Instantiation
                        VariableInstantiation curr_variableInstantiation = parseVariableInstantiation(type, identifier);
                        consume(Token.Semicolon); //; expected
                        statements_of_theblock.add(curr_variableInstantiation);
                    } else {//Variable Declaration
                        consume(Token.Semicolon); //; expected
                        statements_of_theblock.add(new VariableDeclaration(type, new VariableReference(identifier)));
                    }
                }
                else  { //Variable Assignment
                    VariableAssigment curr_variableAssignment = parseVariableAssigment(curr_identifier);
                    consume(Token.Semicolon); //; expected
                    statements_of_theblock.add(curr_variableAssignment); //ad the statement to the block
                }
            } else if (isSymbolOfType(Token.For)) {//FoR loop
                statements_of_theblock.add(parseForStatement());
            } else if(isSymbolOfType(Token.While)){
                statements_of_theblock.add(parseWhileStatement());
            }else if(isSymbolOfType(Token.If)){
                statements_of_theblock.add(parseIfStatement());
            }else if(isSymbolOfType(Token.Return)) {
                statements_of_theblock.add(parseReturnStatement());
            }

        }

        return new Block(statements_of_theblock);
    }

    /*
        Parse a Factor
        Factor -> IntNumber| FloatNumber|BooleanVale|String|Identifier| (Expression) | (Negate) - Expression | ! Expression
        Factor -> Identifier(Parameters) as FunctionCall
        Factor -> BaseType[Expression] as ArrayIntialization
        Factor -> DeclarationOfArray
        DeclarationOfArray -> {Expressions} i.e {1,2,3,4}

     */
    private ExpressionStatement parseFactor() throws IOException, ParserException {

        if(lookahead.isTypeof("IntNumber")){
            return new Value(consume(Token.IntNumber));
        } else if (lookahead.isTypeof("FloatNumber")) {
            return new Value(consume(Token.FloatNumber));
        } else if (lookahead.isTypeof("BooleanValue")) {
            return new Value(consume(Token.BooleanValue));
        }else if (lookahead.isTypeof("String")) {
            return new Value(consume(Token.String));
        }else if (lookahead.isTypeof("Identifier")){
            //if is a identifier can be a struct access, an fuctioncall or a simple variable
            Symbol curr_identifier=consume(Token.Identifier); //Expected
            //if is not a struct access is a function call
            if(isSymbolOfType(Token.OpeningParenthesis)){
                return parseFunctionCall(curr_identifier); //pass the function name we have already consumed
            }//Ok it's only an identifier so a variable
            else return new VariableReference(curr_identifier);
        }else if(isSymbolOfType(Token.BasedType)){
            //Array Initialization
            return parseArrayInitialization();
        }else if (isSymbolOfType(Token.OpeningParenthesis)) {
            consume(Token.OpeningParenthesis); //Expected ( as if statement
            ExpressionStatement subExpr=parseExpression();
            consume(Token.ClosingParenthesis); //Expected )
            return subExpr;
        } else if(lookahead.getValue().equals("-")){//Negative Expression
            consume(Token.AdditiveOperator); //Expected -
            ExpressionStatement expr=parseExpression();
            if(expr instanceof ArithmeticNegationNode) throw new ParserException("Invalid expression: Nested negation operators (-) are not allowed.");
            return new ArithmeticNegationNode(expr);
        } else if(lookahead.getValue().equals("!")){
            consume(Token.LogicalOperator); //Expected !
            //We dont have the same situation of - beacause !!! are allowed so is the negate of the nagate of the nagate of the expression
            ExpressionStatement expr=parseExpression();
            return new BooleanNegationNode(expr);
        } else if(isSymbolOfType(Token.OpeningCurlyBrace)){
            consume(Token.OpeningCurlyBrace); //Expected {
            ArrayList<ExpressionStatement> arrayElements = parseArrayElements();
            consume(Token.ClosingCurlyBrace); //Expected }
            return new ArrayValueDeclaration(arrayElements);
        } else {
            throw new ParserException("Unexpected Symbol "+lookahead);
        }
    }

    /*
        Parse an ArrayValueDeclaration
        ArrayValueDeclaration -> { ArrayElements }
        ArrayElements -> Expression | Expression, ArrayElements | ε
     */

    private ArrayList<ExpressionStatement> parseArrayElements() throws IOException, ParserException {
        ArrayList<ExpressionStatement> ret=new ArrayList<>();
        ExpressionStatement expr;
        boolean more_than_one=false;
        while(!isSymbolOfType(Token.ClosingCurlyBrace)){
            if(more_than_one) consume(Token.Comma); //Expected ,
            if(isSymbolOfType(Token.BasedType)){
                throw new ParserException("You cant initilize baseType variable (int,float,bool) in a array declaration line"+lookahead.getLine());
            }
            expr=parseExpression();
            ret.add(expr);
            more_than_one=true;
        }

        /*
        try{
            //an expression is expected but maybe we have an empty array
            System.out.println(lookahead);
            if(isSymbolOfType(Token.ClosingCurlyBrace)) System.out.println("CIAOOO"); //ok we have no elements
            expr=parseExpression();
            ret.add(expr);
        }catch (ParserException e){
            System.out.println("CIAOOO");
            if(isSymbolOfType(Token.ClosingCurlyBrace)) return ret; //ok we have no elements
            throw e; //ok i need to throw the exception
        }

        while(isSymbolOfType(Token.Comma)){
            consume(Token.Comma); //Expected ,
            expr=parseExpression();
            ret.add(expr);
        }
         */
        return ret;
    }

    /*
        Parse a FunctionCall
        FunctionCall -> Identifier(Parameters)
     */

    private FunctionCall parseFunctionCall(Symbol function_name) throws IOException, ParserException {
        consume(Token.OpeningParenthesis); //Expected (
        ArrayList<ExpressionStatement> parameters=parseParameters();
        consume(Token.ClosingParenthesis); //Expected )
        return new FunctionCall(function_name,parameters);
    }

    /*
        Parse the parameters passed to a function call
        Parameters -> (Expression) | (Expression, Parameters) | ε
     */

    private ArrayList<ExpressionStatement> parseParameters() throws IOException, ParserException {
        ArrayList<ExpressionStatement> ret=new ArrayList<>();
        ExpressionStatement expr;
        boolean more_than_one=false;
        while(!isSymbolOfType(Token.ClosingParenthesis)){
            if(more_than_one) consume(Token.Comma); //Expected ,
            expr=parseExpression();
            ret.add(expr);
            more_than_one=true;
        }
        return ret;

        /*
        try{
            expr=parseExpression();
            ret.add(expr);
        }catch (ParserException e){ //Exception i found a function call without parameters maybe
            if(isSymbolOfType(Token.ClosingParenthesis)) return ret; //ok no parameters
            throw e; //ok i need to throw the exception
        }
        while(isSymbolOfType(Token.Comma)){
            consume(Token.Comma); //Expected ,
            expr=parseExpression();
            ret.add(expr);
        }
        */

    }

    /*
        Parse a ArrayInitialization
        ArrayInitialization -> BaseType[Expression]
     */
    public ArrayInitialization parseArrayInitialization() throws ParserException, IOException {
        Type type=new BaseType(consume(Token.BasedType));
        consume(Token.OpeningSquareBracket); //Expected [
        ExpressionStatement size=parseExpression();
        consume(Token.ClosingSquareBracket); //Expected ]
        return new ArrayInitialization(type,size);
    }
}
