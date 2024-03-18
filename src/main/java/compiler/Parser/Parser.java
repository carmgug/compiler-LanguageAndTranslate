package compiler.Parser;
import compiler.Lexer.*;

import compiler.Parser.AST.ASTNode;
import compiler.Parser.AST.ASTNodes.*;
import compiler.Parser.AST.ASTNodes.Constant;
import compiler.Parser.AST.ASTNodes.ExpressionStatement;
import compiler.Parser.AST.ASTNodes.Expressions.*;
import compiler.Parser.AST.ASTNodes.Expressions.NegationNodes.ArithmeticNegationNode;
import compiler.Parser.AST.ASTNodes.Expressions.NegationNodes.BooleanNegationNode;
import compiler.Parser.AST.ASTNodes.Expressions.Types.ArrayStructType;
import compiler.Parser.AST.ASTNodes.Expressions.Types.ArrayType;
import compiler.Parser.AST.ASTNodes.Expressions.Types.BaseType;
import compiler.Parser.AST.ASTNodes.Expressions.Types.StructType;
import compiler.Parser.AST.ASTNodes.Expressions.Value;
import compiler.Parser.AST.ASTNodes.VariableDeclaration;
import compiler.Parser.AST.ASTNodes.Struct;
import compiler.Parser.AST.Program;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.StringReader;
import java.rmi.UnexpectedException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;

public class Parser {

    private static final Logger LOGGER = LogManager.getLogger(Parser.class.getName());
    private final Lexer lexer;
    private Symbol lookahead;
    public Parser(Lexer lexer) {
        this.lexer=lexer;
    }

    public static void main(String[] args) throws IOException {
        String test="final bool isEmpty = isTrue(isTrue()[4.getARandomNumber().ciao[4]]);\nfinal int a_abc_123_ =  3;\n" +
                "final int a_abc_123_ = 3;\n" +
                "final float j = 3.256*5.0;\n" +
                "final int k = i*3;\n" +
                "final string message = \"Hello\\n\";\n" +
                "final bool isEmpty = isTrue(a[getNumberOfIncrement()[ciao]]);\n" +
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
                "\tperson age=Person(\"carmelo\",\"gugliotta\",24).age;" +
                "int x=3;\n" +
                "def boolean prova(int a, int b){\n" +
                "boolean flag=false;\n" +
                "while(flag==false && true){\n" +
                "int ciao=4;\n" +
                "i=i+1;\n" +
                "ciao(prova(cazzo));\n" +
                "int[] vett;\n" +
                "for(int i=0;i<3;i=i+1){\n}" +
                "}\n" +
                "}\n";

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
            LOGGER.log(Level.DEBUG,"Struct parsed: "+curr_stuct);
            program.addStruct(curr_stuct);
        }
        while(lookahead.getType().equals(Token.BasedType) || (lookahead.getType().equals(Token.Identifier)) ){
            GlobalVariable curr_global_variable= parseGlobalVariable();
            LOGGER.log(Level.DEBUG,"Global Variable parsed: "+curr_global_variable);
            program.addGlobalVariables(curr_global_variable);
            program.addGlobalVariables(curr_global_variable);
        }
        while(lookahead.getValue().equals("def")){
            consume(Token.Keywords); //def consumed
            Procedure curr_procedure=parseProcedure();
            LOGGER.log(Level.DEBUG,"Procedure parsed : "+curr_procedure);
            program.addProcedure(curr_procedure);
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



    /*
        Type -> BasedType | IdentifierType
     */
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


    /*
        IdentifierType -> StructType
        IdentifierType[] -> ArrayStructType
     */
    private Type parseIdentifierType() throws IOException {
        Symbol type=consume(Token.Identifier);
        if(isType(Token.SpecialCharacter) && lookahead.getValue().equals("[")){
            if(!lookahead.getValue().equals("[")) throw new RuntimeException("Expected [ but found: "+lookahead.getValue()); //TODO throw an exception
            consume(Token.SpecialCharacter); //Expected [
            if(!lookahead.getValue().equals("]")) throw new RuntimeException("Expected [ but found: "+lookahead.getValue()); //TODO throw an exception
            consume(Token.SpecialCharacter); //Expected ]
            return new ArrayStructType(type);
        }
        return new StructType(type);
    }



    /*
        BasedType -> BaseType
        BasedType[] -> ArrayType

     */
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
        Parse a struct; a struct is a collection of fields
     */

    private Struct parseStruct() throws IOException {
        Symbol identifier=consume(Token.Identifier);
        consume(Token.SpecialCharacter); //{ expected
        ArrayList<VariableDeclaration> variableDeclarations =parseVariableDeclarationInStruct();
        consume(Token.SpecialCharacter); //} expected
        return new Struct(identifier, variableDeclarations);
    }


    /*
        Parse a list of fields; a field is a variableDeclaration;
     */

    private ArrayList<VariableDeclaration> parseVariableDeclarationInStruct() throws IOException {
        ArrayList<VariableDeclaration> ret=new ArrayList<>();
        while(!lookahead.getValue().equals("}")){
            Type type=parseType();
            Symbol identifier=consume(Token.Identifier);
            consume(Token.SpecialCharacter); // ; expected
            ret.add(new VariableDeclaration(type,identifier));
        }
        return ret;
    }

    /*
        Parse an expression
        Expression -> parseAndOrExpression
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
        could be a ArrayAccess |
        MultiplicativeExpression MultiplyOperator ArrayAccess ->
            ArrayAccess MultiplyOperator ArrayAccess MultiplyOperator ArrayAccess
        +,-, Negative Value (-)
     */

    private ExpressionStatement parseMultiplyExpression() throws IOException {
        ExpressionStatement left = parseArrayAccess();
        while (lookahead.getValue().equals("/")|| lookahead.getValue().equals("*") || lookahead.getValue().equals("%")) {
            Symbol operator = Symbol.copy(lookahead);
            consume(Token.ArithmeticOperator); //Expected Arithmetic Operator
            ExpressionStatement right = parseArrayAccess();
            left = new BinaryExpression(left, operator, right);

        }
        return left;
    }

    /*
        Parse an ArrayAccess
        ArrayAccess -> StructAccess [ Expression ]
     */

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

    /*
        Parse a StructAccess
        StructAccess -> Factor | StructAccess . Factor
     */
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
        else return new StructAccess(struct_access);
    }
    private GlobalVariable parseGlobalVariable() throws IOException {
        Type type=parseType();
        Symbol identifier=consume(Token.Identifier);
        consume(Token.AssignmentOperator); // = expected
        ExpressionStatement expr=parseExpression();
        consume(Token.SpecialCharacter); // ; expected
        return new GlobalVariable(type,identifier,expr);
    }

    private Procedure parseProcedure() throws  IOException{
        Type returnType=parseType(); //TODO - Gestire void
        Symbol procedure_name=consume(Token.Identifier);
        consume(Token.SpecialCharacter); // ( expexted
        ArrayList<VariableDeclaration> parameters_of_the_procedure= parseVariableDeclarationInProcedure();
        consume(Token.SpecialCharacter); // ) expexted
        consume(Token.SpecialCharacter); // { expexted
        Block block= parseBlock();
        consume(Token.SpecialCharacter); // } expexted
        //TODO Consume "return"
        return new Procedure(returnType,procedure_name,parameters_of_the_procedure,block);
    }


    private ArrayList<VariableDeclaration> parseVariableDeclarationInProcedure() throws IOException {
        ArrayList<VariableDeclaration> ret=new ArrayList<>();
        if(!lookahead.getValue().equals(")")){
            Type type=parseType();
            Symbol identifier=consume(Token.Identifier);
            ret.add(new VariableDeclaration(type,identifier));
            while(lookahead.getValue().equals(",")) {
                consume(Token.SpecialCharacter); // , expected
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

    private VariableDeclaration parseVariableDeclaration() throws IOException {
        Type type = parseType();
        Symbol identifier = consume(Token.Identifier);
        if (lookahead.getValue().equals("=")) {
            //Variable Instantiation
            return parseVariableInstantiation(type, identifier);
        }
        //Variable Declaration
        consume(Token.SpecialCharacter); //; expected
        return new VariableDeclaration(type, identifier);
    }

    /*
        Parse a Variable Instantiation
        VariableInstantiation -> Type Identifier = Expression;
     */
    private VariableInstantiation parseVariableInstantiation(Type type,Symbol identifier) throws IOException {
        consume(Token.AssignmentOperator);
        ExpressionStatement exp = parseExpression();
        consume(Token.SpecialCharacter); //; expected
        return new VariableInstantiation(type,identifier, exp);
    }

    /*
        Parse a Variable Assigment
        VariableAssigment -> Identifier = Expression;
     */

    private VariableAssigment parseVariableAssigment(Symbol identifier) throws IOException {
        consume(Token.AssignmentOperator);
        ExpressionStatement exp = parseExpression();
        consume(Token.SpecialCharacter); //; expected
        return new  VariableAssigment(identifier, exp);
    }

    /*
        Parse a For statement
        ForStatement -> for (Type Identifier = Expression; Expression; VariableAssigment) {Block}
     */

    private ForStatement parseForStatement() throws IOException {
        consume(Token.Keywords); //for
        consume(Token.SpecialCharacter); //( expected
        ASTNode start;
        Type type = parseType();
        if(lookahead.getValue().equals("=")){
            //Variable Assigment
            start=parseVariableAssigment(type.getSymbol());
        }else{
            //Variable Instantiation
            Symbol identifier = consume(Token.Identifier);
            start=parseVariableInstantiation(type, identifier);
        }
        ExpressionStatement condition=parseExpression();
        consume(Token.SpecialCharacter); //; expected
        VariableAssigment update=parseVariableAssigment(consume(Token.Identifier));
        consume(Token.SpecialCharacter); //) expected
        Block block = parseBlock();
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
    private WhileStatement parseWhileStatement() throws IOException {
        consume(Token.Keywords); //while
        consume(Token.SpecialCharacter); //( expexted
        ExpressionStatement condition=parseExpression();
        consume(Token.SpecialCharacter); //) expexted
        consume(Token.SpecialCharacter); // { expexted
        Block block = parseBlock();
        consume(Token.SpecialCharacter); // } expexted
        return new WhileStatement(condition,block);
    }

    /*
        Parse an if statement
        IfStatement -> if (Expression) Block | if (Expression) Block else Block
     */
    private IfStatement parseIfStatement() throws IOException {
        consume(Token.Keywords); //if
        consume(Token.SpecialCharacter); //( expected
        ExpressionStatement if_condition=parseExpression();
        consume(Token.SpecialCharacter); //) expected
        consume(Token.SpecialCharacter); // { expected
        Block if_block = parseBlock();
        consume(Token.SpecialCharacter); // } expected
        if(lookahead.getValue().equals("else")){
            consume(Token.Keywords); //else expected
            consume(Token.SpecialCharacter); // { expected
            Block else_block = parseBlock();
            consume(Token.SpecialCharacter); // } expected
            return new IfElseStatement(if_condition,if_block,else_block);
        }
        return new IfStatement(if_condition,if_block);
    }




    private Block parseBlock() throws IOException {

        /*TODO  - Gestire la differenza tra global variable, fields e variable Assignment.
                -tutti possono iniziare con un Token.Identifier

         */

        /*TODO -Cosa succede se lo statment di return è contenuto all'interno di un ciclo?
               -Si deve includere il costutto else if?

         */

        /*TODO
                Chiamare errore se un return segue una return
         */



        /*
            Instance of a variable con BaseType

        person a=3;
        person a;

         */
        ArrayList<ASTNode> statements_of_theblock=new ArrayList<>();

        while(!lookahead.getValue().equals("}")) { //waiting for the }
            if (lookahead.getType().equals(Token.BasedType)) {
                //We can have Variable Instantiation or Variable Declaration
                //Variable Instantiation e Variable Declarion BaseType
                VariableDeclaration curr_variable=parseVariableDeclaration();
                statements_of_theblock.add(curr_variable);
            } else if (lookahead.getType().equals(Token.Identifier)) {
                // We can have Variable Instantiation or Variable Declaration or We can have Function Call or Variable Assignment
                Type parsedType = parseType();
                if (lookahead.getValue().equals("=")) { //Variable Assignment
                    VariableAssigment curr_variableAssignment = parseVariableAssigment(parsedType.getSymbol());
                    statements_of_theblock.add(curr_variableAssignment); //ad the statement to the block
                } else if (lookahead.getValue().equals("(")) { //FunctionCall
                    //FunctionCall
                    Symbol curr_identifier = parsedType.getSymbol(); //Torno indietro nella mia scelta
                    FunctionCall curr_functionCall = parseFunctionCall(curr_identifier);
                    consume(Token.SpecialCharacter); //; expected
                    statements_of_theblock.add(curr_functionCall);
                } else {
                    //Variable Instantiation or Variable Declaration
                    Symbol identifier = consume(Token.Identifier);
                    if (lookahead.getValue().equals("=")) {
                        VariableInstantiation curr_variableInstantiation = parseVariableInstantiation(parsedType, identifier);
                        statements_of_theblock.add(curr_variableInstantiation);
                    }else{//Variable Declaration
                        consume(Token.SpecialCharacter); //; expected
                        statements_of_theblock.add(new VariableDeclaration(parsedType,identifier));
                    }


                }
            } else if (lookahead.getValue().equals("for")) {//FoR loop
                statements_of_theblock.add(parseForStatement());
            } else if(lookahead.getValue().equals("while")){
                statements_of_theblock.add(parseWhileStatement());
            }else if(lookahead.getValue().equals("if")){
                statements_of_theblock.add(parseIfStatement());
            }

        }

        return new Block(statements_of_theblock);
    }

    /*
        Parse a Factor
        Factor -> IntNumber| FloatNumber|BooleanVale|String|Identifier| (Expression) | (Negate) - Expression | ! Expression
        Factor -> Identifier(Parameters) as FunctionCall
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
            //if is a identifier can be a struct access, an fuctioncall or a simple variable
            Symbol curr_identifier=consume(Token.Identifier); //Expected
            //if is not a struct access is a function call
            if(lookahead.getValue().equals("(")){
                return parseFunctionCall(curr_identifier); //pass the function name we have already consumed
            }//Ok it's only an identifier so a variable
            else return new Value(curr_identifier);
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
            throw new RuntimeException("Unexpected Symbol "+lookahead); //TODO throw an exception
        }
    }

    private FunctionCall parseFunctionCall(Symbol function_name) throws IOException {
        consume(Token.SpecialCharacter); //Expected (
        ArrayList<ExpressionStatement> parameters=parseParameters();
        if(!lookahead.getValue().equals(")")) throw new RuntimeException("Non ho trovato la parentesi chiusa");//TODO throw an exception
        consume(Token.SpecialCharacter); //Expected )
        return new FunctionCall(function_name,parameters);
    }

    /*
        Parse the parameters passed to a function call
        Parameters -> () | (Expression) | (Expression, Parameters)
     */
    private ArrayList<ExpressionStatement> parseParameters() throws IOException {
        ArrayList<ExpressionStatement> ret=new ArrayList<>();
        ExpressionStatement expr;
        try{
            expr=parseExpression();
            ret.add(expr);
        }catch (Exception e){ //Exception i found a function call without parameters maybe
            if(lookahead.getValue().equals(")")) return ret; //ok no parameters
            throw e; //ok i need to throw the exception
        }
        while(lookahead.getValue().equals(",")){
            consume(Token.SpecialCharacter); //Expected , //TODO
            expr=parseExpression();
            ret.add(expr);
        }
        return ret;

    }













}
