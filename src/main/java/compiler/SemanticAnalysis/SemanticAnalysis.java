package compiler.SemanticAnalysis;

import compiler.Exceptions.ParserExceptions.ParserException;
import compiler.Exceptions.SemanticException.SemanticException;
import compiler.Lexer.Symbol;
import compiler.Lexer.Token;
import compiler.Parser.AST.ASTNode;
import compiler.Parser.AST.ASTNodes.Expressions.Type;
import compiler.Parser.AST.ASTNodes.Expressions.Types.ArrayType;
import compiler.Parser.AST.ASTNodes.Expressions.Types.BaseType;
import compiler.Parser.AST.ASTNodes.Expressions.Types.VoidType;
import compiler.Parser.AST.Program;
import compiler.Parser.Parser;
import compiler.SemanticAnalysis.SymbolTable.SymbolTable;
import compiler.SemanticAnalysis.SymbolTable.SymbolTableValues.SymbolTableProcedureType;
import compiler.SemanticAnalysis.SymbolTable.SymbolTableValues.SymbolTableProceduresEntry;
import compiler.SemanticAnalysis.SymbolTable.SymbolTableValues.SymbolTableType;
import compiler.SemanticAnalysis.Visitor.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

public class SemanticAnalysis {

    private final Parser parser;
    private final SymbolTable globalTable= new SymbolTable();
    private final SymbolTable structTable= new SymbolTable();
    private final SymbolTableUpdater symbolTableUpdater=new SymbolTableUpdater();
    private final SemanticAnalysisVisitor semanticAnalysisVisitor=new SemanticAnalysisVisitor();
    private static final Logger LOGGER = LogManager.getLogger(SemanticAnalysis.class.getName());
    private final boolean debugSemanticAnalysis;


    public SemanticAnalysis(Reader r){
        this.parser= new Parser(r);
        this.debugSemanticAnalysis=false;;
    }

    public SemanticAnalysis(Reader r,boolean debugLexer,boolean debugParser,boolean debugSemanticAnalysis) {
        this.debugSemanticAnalysis=debugSemanticAnalysis;
        this.parser= new Parser(r,debugLexer,debugParser);
    }


    public Program performSemanticAnalysis() throws SemanticException, ParserException, IOException {
        Program p=parser.getAST2();
        intilizeSymbolTable(p);
        performSemanticAnalysis(p);
        return p;
    }

    public SymbolTable getGlobalTable(){
        return this.globalTable;
    }

    public SymbolTable getStructTable(){
        return this.structTable;
    }


    public void intilizeSymbolTable(Program program) throws SemanticException {
        //Dobbiamo prima di tutto inserire le funzioni di base
        addBasicProcedure();
        Iterator<ASTNode> it= program.iterator(); //Si itera sull'AST creato dal Parser
        while(it.hasNext()){
            ASTNode next=it.next();
            if(debugSemanticAnalysis){
                LOGGER.info("Adding to the symbol Table :\n\t\t "+next);
            }
            next.accept(symbolTableUpdater,globalTable,structTable);
        }
        if(debugSemanticAnalysis) {
            LOGGER.info("Symbol Table and Struct Table after the initialization");
            LOGGER.info("Symbol Table: \n" + globalTable);
            LOGGER.info("Struct Table: \n" + structTable);
        }

    }

    public void performSemanticAnalysis(Program program) throws SemanticException {
        Iterator<ASTNode> it= program.iterator();
        while(it.hasNext()){
            ASTNode next=it.next();
            if(debugSemanticAnalysis){
                LOGGER.info("Performing Semantic Analysis on :\n\t\t "+next);
            }
            next.accept(semanticAnalysisVisitor,globalTable,structTable);
        }
        if(debugSemanticAnalysis) {
            LOGGER.info("Symbol Table and Struct Table after the semantic analysis");
            LOGGER.info("Symbol Table: \n" + globalTable);
            LOGGER.info("Struct Table: \n" + structTable);
        }
    }

    private void addBasicProcedure(){
        addChrProcedure();
        addLenProcedure();
        addFloorProcedure();
        //readInt, readFloat, readString, writeInt(int), writeFloat(float), write(string), writeln()
        addReadInt();
        addReadFloat();
        addReadString();
        addWriteInt();
        addWriteFloat();
        addWrite();
        addWriteln();


    }

    private void addReadInt(){
        String procedure_name="readInt";
        Type returnType=new BaseType(new Symbol(Token.IntType,"int"));

        SymbolTableProcedureType function=new SymbolTableProcedureType(new SymbolTableType(returnType));

        SymbolTableProceduresEntry procedures=new SymbolTableProceduresEntry();
        procedures.addFunction(function);
        this.globalTable.add(procedure_name,procedures);
    }

    private void addReadFloat(){
        String procedure_name="readFloat";
        Type returnType=new BaseType(new Symbol(Token.FloatType,"float"));

        SymbolTableProcedureType function=new SymbolTableProcedureType(new SymbolTableType(returnType));

        SymbolTableProceduresEntry procedures=new SymbolTableProceduresEntry();
        procedures.addFunction(function);
        this.globalTable.add(procedure_name,procedures);
    }

    private void addReadString(){
        String procedure_name="readString";
        Type returnType=new BaseType(new Symbol(Token.StringType,"string"));

        SymbolTableProcedureType function=new SymbolTableProcedureType(new SymbolTableType(returnType));

        SymbolTableProceduresEntry procedures=new SymbolTableProceduresEntry();
        procedures.addFunction(function);
        this.globalTable.add(procedure_name,procedures);
    }

    private void addWriteInt(){
        String procedure_name="writeInt";
        Type returnType=new VoidType(new Symbol(Token.Void,"void"));
        Type param1=new BaseType(new Symbol(Token.IntType,"int"));
        SymbolTableProcedureType function=new SymbolTableProcedureType(new SymbolTableType(returnType));
        function.addParameter("int",new SymbolTableType(param1));

        SymbolTableProceduresEntry procedures=new SymbolTableProceduresEntry();
        procedures.addFunction(function);
        this.globalTable.add(procedure_name,procedures);
    }

    private void addWriteFloat(){
        String procedure_name="writeFloat";
        Type returnType=new VoidType(new Symbol(Token.Void,"void"));
        Type param1=new BaseType(new Symbol(Token.FloatType,"float"));
        SymbolTableProcedureType function=new SymbolTableProcedureType(new SymbolTableType(returnType));
        function.addParameter("float",new SymbolTableType(param1));

        SymbolTableProceduresEntry procedures=new SymbolTableProceduresEntry();
        procedures.addFunction(function);
        this.globalTable.add(procedure_name,procedures);
    }

    private void addWrite(){
        String procedure_name="write";
        Type returnType=new VoidType(new Symbol(Token.Void,"void"));
        SymbolTableProcedureType function=new SymbolTableProcedureType(new SymbolTableType(returnType));
        function.addParameter("string",new SymbolTableType(new BaseType(new Symbol(Token.StringType,"string"))));

        SymbolTableProcedureType function2=new SymbolTableProcedureType(new SymbolTableType(returnType));
        function2.addParameter("int",new SymbolTableType(new BaseType(new Symbol(Token.IntType,"int"))));

        SymbolTableProcedureType function3=new SymbolTableProcedureType(new SymbolTableType(returnType));
        function3.addParameter("float",new SymbolTableType(new BaseType(new Symbol(Token.FloatType,"float"))));

        SymbolTableProcedureType function4=new SymbolTableProcedureType(new SymbolTableType(returnType));
        function4.addParameter("bool",new SymbolTableType(new BaseType(new Symbol(Token.BoolType,"bool"))));

        SymbolTableProceduresEntry procedures=new SymbolTableProceduresEntry();
        procedures.addFunction(function);
        procedures.addFunction(function2);
        procedures.addFunction(function3);
        procedures.addFunction(function4);
        this.globalTable.add(procedure_name,procedures);
    }

    private void addWriteln(){
        String procedure_name="writeln";
        Type returnType=new VoidType(new Symbol(Token.Void,"void"));

        SymbolTableProcedureType function1=new SymbolTableProcedureType(new SymbolTableType(returnType));

        SymbolTableProcedureType function2=new SymbolTableProcedureType(new SymbolTableType(returnType));
        function2.addParameter("string",new SymbolTableType(new BaseType(new Symbol(Token.StringType,"string"))));

        SymbolTableProcedureType function3=new SymbolTableProcedureType(new SymbolTableType(returnType));
        function3.addParameter("int",new SymbolTableType(new BaseType(new Symbol(Token.IntType,"int"))));

        SymbolTableProcedureType function4=new SymbolTableProcedureType(new SymbolTableType(returnType));
        function4.addParameter("float",new SymbolTableType(new BaseType(new Symbol(Token.FloatType,"float"))));

        SymbolTableProcedureType function5=new SymbolTableProcedureType(new SymbolTableType(returnType));
        function5.addParameter("bool",new SymbolTableType(new BaseType(new Symbol(Token.BoolType,"bool"))));

        SymbolTableProceduresEntry procedures=new SymbolTableProceduresEntry();
        procedures.addFunction(function1);
        procedures.addFunction(function2);
        procedures.addFunction(function3);
        procedures.addFunction(function4);
        procedures.addFunction(function5);

        this.globalTable.add(procedure_name,procedures);
    }

    private void addChrProcedure(){
        String procedure_name="chr";
        Type returnType=new BaseType(new Symbol(Token.StringType,"string"));
        Type param1=new BaseType(new Symbol(Token.IntType,"int"));

        SymbolTableProcedureType function=new SymbolTableProcedureType(new SymbolTableType(returnType));
        function.addParameter("int",new SymbolTableType(param1));

        SymbolTableProceduresEntry procedures=new SymbolTableProceduresEntry();
        procedures.addFunction(function);
        this.globalTable.add(procedure_name,procedures);

    }
    private void addLenProcedure(){
        String procedure_name="len";
        Type returnType=new BaseType(new Symbol(Token.IntType,"int"));

        SymbolTableProcedureType function1=new SymbolTableProcedureType(new SymbolTableType(returnType));
        function1.addParameter("string",new SymbolTableType(new BaseType(new Symbol(Token.StringType,"string"))));

        SymbolTableProcedureType function2=new SymbolTableProcedureType(new SymbolTableType(returnType));
        function2.addParameter("int[]",new SymbolTableType(new ArrayType(new Symbol(Token.IntType,"int"))));

        SymbolTableProcedureType function3=new SymbolTableProcedureType(new SymbolTableType(returnType));
        function3.addParameter("float[]",new SymbolTableType(new ArrayType(new Symbol(Token.FloatType,"float"))));

        //Add also for the bool array
        SymbolTableProcedureType function4=new SymbolTableProcedureType(new SymbolTableType(returnType));
        function4.addParameter("bool[]",new SymbolTableType(new ArrayType(new Symbol(Token.BoolType,"bool"))));

        //Add also for the array of String
        SymbolTableProcedureType function5=new SymbolTableProcedureType(new SymbolTableType(returnType));
        function5.addParameter("string[]",new SymbolTableType(new ArrayType(new Symbol(Token.StringType,"string"))));
        
        SymbolTableProceduresEntry procedures=new SymbolTableProceduresEntry();
        procedures.addFunction(function1);
        procedures.addFunction(function2);
        procedures.addFunction(function3);
        procedures.addFunction(function4);
        procedures.addFunction(function5);
        this.globalTable.add(procedure_name,procedures);

    }
    private void addFloorProcedure(){
        String procedure_name="floor";
        Type returnType=new BaseType(new Symbol(Token.IntType,"int"));

        SymbolTableProcedureType function=new SymbolTableProcedureType(new SymbolTableType(returnType));
        function.addParameter("float",new SymbolTableType(new BaseType(new Symbol(Token.FloatType,"float"))));

        SymbolTableProceduresEntry procedures=new SymbolTableProceduresEntry();
        procedures.addFunction(function);
        this.globalTable.add(procedure_name,procedures);
    }



}



