package compiler.SemanticAnalysis.Visitor;

import compiler.Exceptions.SemanticException.SemanticErrorException;
import compiler.Exceptions.SemanticException.TypeErrorException;
import compiler.Lexer.Token;
import compiler.Parser.AST.ASTNodes.*;
import compiler.Parser.AST.ASTNodes.Expressions.Type;
import compiler.SemanticAnalysis.SemanticType;
import compiler.SemanticAnalysis.SemanticTypes.SemanticProceduresEntry;
import compiler.SemanticAnalysis.SemanticTypes.SemanticRecordType;
import compiler.SemanticAnalysis.SemanticTypes.SemanticStructType;
import compiler.SemanticAnalysis.SymbolTable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * The SymbolTableUpdater class implements the Visitor interface to update the symbol table.
 * It provides methods to visit different types of elements and update the symbol table accordingly.
 */
public class SymbolTableUpdater implements Visitor {


    @Override
    public void visit(Constant constant, SymbolTable symbolTable) throws SemanticErrorException {
        String id=constant.getIdentifier().getValue(); //Si preleva l'identifier
        Type type=constant.getType(); //Si preleva il tipo
        //Si verifica che non sia gia stata definita una variabile con stesso nome
        if(symbolTable.get(id)!=null){ //if it's already defined then you need to throw an Exception
            throw new SemanticErrorException("Constant "+id +" already defined "+ "(line "+constant.getIdentifier().getLine()+")");
        }
        symbolTable.add(id,new SemanticType(type.getSymbol().getValue()));
    }

    @Override
    public void visit(Struct struct, SymbolTable symbolTable) throws SemanticErrorException {
        String structName= struct.getIdentifier().getValue();
        if(symbolTable.get(structName)!=null){ //check if ther is another struct with the same name
            throw new SemanticErrorException("Struct "+structName +" already defined "+ "(line "+struct.getIdentifier().getLine()+")");
        }
        symbolTable.add(structName,new SemanticStructType(structName));
    }

    @Override
    public void visit(GlobalVariable globalVariable, SymbolTable symbolTable) throws SemanticErrorException  {
        String identifier=globalVariable.getIdentifier().getValue(); //Si preleva l'identifier
        Type type=globalVariable.getType(); //Si preleva il tipo
        //Si verifica che non sia gia stata definita una variabile con stesso nome
        if(symbolTable.get(identifier)!=null){ //if it's already defined then you need to throw an Exception
            throw new SemanticErrorException("Constant "+identifier +" already defined "+ "(line "+globalVariable.getIdentifier().getLine()+")");
        }
        symbolTable.add(identifier,new SemanticType(type.getSymbol().getValue()));
    }

    @Override
    public void visit(ExpressionStatement expressionStatement, SymbolTable symbolTable) throws SemanticErrorException {
        throw new SemanticErrorException("Should not run, in this part");
    }


    @Override
    public void visit(Procedure procedure, SymbolTable symbolTable) throws SemanticErrorException {
        Type return_type=procedure.getReturnType(); //Si preleva il tipo
        String procedure_identifier=procedure.getName().getValue();
        ArrayList<VariableDeclaration> procedure_parameters=procedure.getParameters_of_the_procedure();
        //Io prendo la entry delle procedure con lo stesso nome stesso return type ma diversi parametri
        SemanticProceduresEntry procedures=(SemanticProceduresEntry) symbolTable.get(procedure_identifier);
        //TODO POTREBBE RITORNARE UNA COSTANTE CHE HA LO STESSO NOME
        if(procedures!=null){
            //Procedure have been already defined
            //Ok we have two procedure with two same name
            //but the list of parameter need to be different
            if(procedures.containsAProcedureWithParameters(procedure_parameters)){
                throw new SemanticErrorException("You have already define a procedure with the same name and same parameters"+
                        "(line: "+procedure.getName().getLine()+")");
            }
            //ok not exist the same procedure we need to add
            procedures.addFunction(procedure_parameters,new SemanticType(return_type.getSymbol().getValue()));
            return;
        }
        procedures=new SemanticProceduresEntry("procedures");
        procedures.addFunction(procedure_parameters,new SemanticType(return_type.getSymbol().getValue()));
        symbolTable.add(procedure_identifier,procedures);

    }

    @Override
    public void visit(Block block, SymbolTable symbolTable) {

    }

    @Override
    public void visit(IfStatement ifStatement, SymbolTable symbolTable) {

    }

    @Override
    public void visit(ForStatement forStatement, SymbolTable symbolTable) {

    }

    @Override
    public void visit(WhileStatement whileStatement, SymbolTable symbolTable) {

    }

    @Override
    public void visit(VariableDeclaration variableDeclaration, SymbolTable symbolTable) {

    }

    @Override
    public void visit(VariableAssigment variableAssigment, SymbolTable symbolTable) {

    }

    @Override
    public void visit(ReturnStatement returnStatement,SymbolTable symbolTable){

    }
}
