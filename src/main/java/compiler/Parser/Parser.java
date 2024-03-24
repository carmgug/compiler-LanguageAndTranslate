package compiler.Parser;
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
                "int x=3;\n" +
                "def void prova(int a, int b){\n" +
                "boolean flag=false;\n" +
                "while(flag==false && true){\n" +
                "int ciao=4;\n" +
                "i=i+1;\n" +
                "i++;\n" +
                "ciao(prova(cazzo));\n" +
                "int[] vett;\n" +
                "for(int i=0,i<3,i++){return;\n}" +
                "}}\n"+
                "Person i=Person(\"carmelo\",\"gugliotta\",24);\n" ;

        String test2="def void ciao(){for(int i=0,i<3,i++){return a*b;} return a*b;}";
        StringReader stringReader= new StringReader(test);
        Parser p= new Parser(stringReader,true,true);
        Program program=p.getAST();
    }

    /*
        @return the Abstract Syntax Tree
     */
    public Program getAST() throws IOException, ParserException {
        Program program = new Program();
        //Take the first token. Lookahead is for predective parsing
        lookahead = lexer.getNextSymbol();
        //First we have to parse the constants
        while(isSymbolOfType(Token.Final)){
            Constant curr_constant=parseConstant();
            if(debugParser) LOGGER.log(Level.DEBUG,"Constant parsed: "+curr_constant);
            program.addConstant(curr_constant);
        }
        while(isSymbolOfType(Token.Struct)){
            consume(Token.Struct); //struct consumed
            Struct curr_stuct=parseStruct();
            if(debugParser) LOGGER.log(Level.DEBUG,"Struct parsed: "+curr_stuct);
            program.addStruct(curr_stuct);
        }

        while(isSymbolOfType(Token.BasedType) || isSymbolOfType(Token.Identifier) || isSymbolOfType(Token.Def)){

            if(isSymbolOfType(Token.BasedType) || isSymbolOfType(Token.Identifier)){ //Parse Global Variable
                GlobalVariable curr_global_variable= parseGlobalVariable();
                if(debugParser) LOGGER.log(Level.DEBUG,"Global Variable parsed: "+curr_global_variable);
                program.addGlobalVariables(curr_global_variable);
            } else if(isSymbolOfType(Token.Def)){ //Parse Procedure
                consume(Token.Def); //def consumed
                Procedure curr_procedure=parseProcedure();
                if(debugParser) LOGGER.log(Level.DEBUG,"Procedure parsed : "+curr_procedure);
                program.addProcedure(curr_procedure);
            }
        }
        if(!isSymbolOfType(Token.EOF)) throw new ParserException("Unexpected token: "+lookahead.getType()+" expected: "+Token.EOF);
        return program;
    }
    /*
        @param token: the type of Symbol to check
        @return true if the current symbol is of the type token
     */
    private boolean isSymbolOfType(Token token){
        return lookahead.getType().equals(token);
    }
    /*
        @param token: the type of Symbol to consume
        @return the current symbol
     */
    private Symbol consume(Token token) throws ParserException, IOException{
        Symbol curr_symbol=lookahead;
        if (!lookahead.getType().equals(token)){
            throw new ParserException("Unexpected token: "+lookahead.getValue()+" expected: "+token);
        }
        lookahead = lexer.getNextSymbol();
        return curr_symbol;
    }


    /*
        Type -> BasedType | IdentifierType | Void
     */
    private Type parseType() throws IOException, ParserException {

        switch (lookahead.getType()){
            case BasedType:
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
            ret.add(new VariableDeclaration(type,identifier));
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
            ret.add(new VariableDeclaration(type,identifier));
            while(isSymbolOfType(Token.Comma)) {
                consume(Token.Comma); // , expected
                type=parseType();
                identifier=consume(Token.Identifier);
                ret.add(new VariableDeclaration(type,identifier));
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
        return new VariableDeclaration(type, identifier);
    }

    /*
        Parse a Variable Instantiation
        VariableInstantiation -> Type Identifier = Expression;
     */
    private VariableInstantiation parseVariableInstantiation(Type type,Symbol identifier) throws IOException, ParserException {
        consume(Token.AssignmentOperator);
        ExpressionStatement exp = parseExpression();
        return new VariableInstantiation(type,identifier, exp);
    }

    /*
        Parse a Variable Assigment
        VariableAssigment -> Identifier = Expression; | Identifier IncrementOperator;
     */

    private VariableAssigment parseVariableAssigment(Symbol identifier) throws IOException, ParserException {
        if(isSymbolOfType(Token.IncrementOperator)){
            consume(Token.IncrementOperator);
            return new VariableAssigment(identifier,new BinaryExpression(
                    new Value(identifier),
                    new Symbol(Token.AdditiveOperator,"+", identifier.getLine()),
                    new Value(new Symbol(Token.IntNumber,"1",identifier.getLine()))));
        }
        consume(Token.AssignmentOperator);
        ExpressionStatement exp = parseExpression();
        return new  VariableAssigment(identifier, exp);
    }

    /*
        Parse a For statement
        ForStatement -> for (Type Identifier = Expression | epsilon, Expression | epsilon, VariableAssigment | epsilon) {Block}
        for(,,)

        for(int i=0,,i++)

        int i =0
        for(,i<n,i++)
     */

    private ForStatement parseForStatement() throws IOException, ParserException {
        consume(Token.For); //for
        consume(Token.OpeningParenthesis); //( expected
        ASTNode start;
        Type type = parseType();
        if(isSymbolOfType(Token.AssignmentOperator) || isSymbolOfType(Token.IncrementOperator)){
            //Variable Assigment
            start=parseVariableAssigment(type.getSymbol());
            consume(Token.Comma); //, expected
        }else{
            //Variable Instantiation
            Symbol identifier = consume(Token.Identifier);
            start=parseVariableInstantiation(type, identifier);
            consume(Token.Comma); //, expected
        }
        ExpressionStatement condition=parseExpression();
        consume(Token.Comma); //, expected
        VariableAssigment update=parseVariableAssigment(consume(Token.Identifier));
        consume(Token.ClosingParenthesis); //) expected
        consume(Token.OpeningCurlyBrace); // { expected
        Block block = parseBlock();
        consume(Token.ClosingCurlyBrace); // } expected
        if(start instanceof VariableAssigment){
           return new ForStatementVariableAssigment((VariableAssigment) start,condition,update,block);
        }
        return new ForStatementVariableInstantiation((VariableInstantiation) start,condition,update,block);


        //; expected
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
                Type parsedType = parseType();
                if (isSymbolOfType(Token.AssignmentOperator) || isSymbolOfType(Token.IncrementOperator)) { //Variable Assignment
                    VariableAssigment curr_variableAssignment = parseVariableAssigment(parsedType.getSymbol());
                    consume(Token.Semicolon); //; expected
                    statements_of_theblock.add(curr_variableAssignment); //ad the statement to the block
                } else if (isSymbolOfType(Token.OpeningParenthesis)) { //FunctionCall
                    //FunctionCall
                    Symbol curr_identifier = parsedType.getSymbol(); //Torno indietro nella mia scelta
                    FunctionCall curr_functionCall = parseFunctionCall(curr_identifier);
                    consume(Token.Semicolon); //; expected
                    statements_of_theblock.add(curr_functionCall);
                } else {
                    //Variable Instantiation or Variable Declaration
                    Symbol identifier = consume(Token.Identifier);
                    if (isSymbolOfType(Token.AssignmentOperator)) { //Variable Instantiation
                        VariableInstantiation curr_variableInstantiation = parseVariableInstantiation(parsedType, identifier);
                        consume(Token.Semicolon); //; expected
                        statements_of_theblock.add(curr_variableInstantiation);
                    }else{//Variable Declaration
                        consume(Token.Semicolon); //; expected
                        statements_of_theblock.add(new VariableDeclaration(parsedType,identifier));
                    }
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
            else return new Value(curr_identifier);
        }else if(lookahead.isTypeof("BasedType")){
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
        try{
            expr=parseExpression();
            ret.add(expr);
        }catch (ParserException e){
            if(isSymbolOfType(Token.ClosingCurlyBrace)) return ret; //ok we have no elements
            throw e; //ok i need to throw the exception
        }
        while(isSymbolOfType(Token.Comma)){
            consume(Token.Comma); //Expected ,
            expr=parseExpression();
            ret.add(expr);
        }
        return ret;
    }

    /*
        Parse a FunctionCall
        FunctionCall -> Identifier(Parameters)
     */

    private FunctionCall parseFunctionCall(Symbol function_name) throws IOException, ParserException {
        consume(Token.OpeningParenthesis); //Expected (
        ArrayList<ExpressionStatement> parameters=parseParameters();
        LOGGER.log(Level.DEBUG,"Function Call: "+function_name+" with parameters: "+parameters);
        System.out.println(lookahead);
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
        return ret;
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




    /*
        //OLD METHODS

    public ExpressionStatement parseArrayAccess() throws IOException {
        ExpressionStatement ArrayName=parseStructAccess();
        //maybe can be an ArrayAccess Otherwise is a simple Factor
        if(lookahead.getValue().equals("[")){
            consume(Token.SpecialCharacter); //Expected [
            ExpressionStatement index;
            try{
                index=parseExpression();
            }catch (Exception e){
                throw new RuntimeException("Non ho trovato l'indice dell'array");//TODO throw an exception
            }
            if(!lookahead.getValue().equals("]")) throw new RuntimeException("Expected ] but found: "+lookahead.getValue()); //TODO throw an exception
            consume(Token.SpecialCharacter); //Expected ]
            return new ArrayAccess(ArrayName,index);
        }
        return ArrayName;
    }

        //Parse a StructAccess
        //StructAccess -> Factor | StructAccess . Factor | ArrayAccess . Factor

    private ExpressionStatement parseStructAccess() throws IOException {
        ExpressionStatement leftPart=parseFactor();
        LinkedList<ExpressionStatement> struct_access=new LinkedList<>();
        struct_access.add(leftPart);
        while (lookahead.getValue().equals(".")) {
            consume(Token.SpecialCharacter); //Expected
            leftPart=parseFactor();
            struct_access.add(leftPart);
        }
        if(struct_access.size()==1) return struct_access.get(0);
        //else return new StructAccess(struct_access);
        return null;

    }
    */
}
