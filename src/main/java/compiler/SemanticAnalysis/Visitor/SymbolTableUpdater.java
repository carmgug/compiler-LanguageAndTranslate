package compiler.SemanticAnalysis.Visitor;

import compiler.Exceptions.SemanticException.ScopeError;
import compiler.Exceptions.SemanticException.SemanticException;
import compiler.Exceptions.SemanticException.StructError;
import compiler.Parser.AST.ASTNode;
import compiler.Parser.AST.ASTNodes.*;
import compiler.Parser.AST.ASTNodes.Expressions.FunctionCall;
import compiler.Parser.AST.ASTNodes.Expressions.Type;
import compiler.Parser.AST.ASTNodes.Expressions.Types.StructType;
import compiler.SemanticAnalysis.SymbolTable.SymbolTableValues.SymbolTableProceduresEntry;
import compiler.SemanticAnalysis.SymbolTable.SymbolTableValues.SemanticStructType;
import compiler.SemanticAnalysis.SymbolTable.SymbolTable;
import compiler.SemanticAnalysis.SymbolTable.SymbolTableValues.SymbolTableType;

import java.util.ArrayList;


/**
 * The SymbolTableUpdater class implements the Visitor interface to update the symbol table.
 * It provides methods to visit different types of elements and update the symbol table accordingly.
 */
public class SymbolTableUpdater implements Visitor {


    private String[] default_types = {"int", "float", "bool",
    "string", "int", "for", "while"};



    @Override
    public void visit(Constant constant, SymbolTable symbolTable,SymbolTable structTable) throws SemanticException {
        String id=constant.getIdentifier().getValue(); //Si preleva l'identifier
        Type type=constant.getType(); //Si preleva il tipo
        //Si verifica che non sia gia stata definita una variabile con stesso nome
        if(symbolTable.get(id)!=null){ //if it's already defined then you need to throw an Exception
            throw new ScopeError("Constant "+id +" already defined "+ "(line "+constant.getLine()+")");
        }
        symbolTable.add(id,new SymbolTableType(type));
    }

    @Override
    public void visit(Struct struct, SymbolTable symbolTable,SymbolTable structTable) throws SemanticException {
        String structName= struct.getIdentifier().getValue();

        if(contains(default_types, structName)){
            throw new StructError("Struct "+structName +" already defined as a default type "+ "(line "+struct.getLine()+")");
        }
        if(structTable.get(structName)!=null){ //check if ther is another struct with the same name
            throw new StructError("Struct "+structName +" already defined "+ "(line "+struct.getLine()+")");
        }
        structTable.add(structName,new SemanticStructType(new StructType(struct.getIdentifier())));
    }

    private boolean contains(String[] arr, String target) {
        for(String s: arr){
            if(s.equals(target)){
                return true;
            }
        }
        return false;
    }

    @Override
    public void visit(GlobalVariable globalVariable, SymbolTable symbolTable,SymbolTable structTable) throws SemanticException {
        String identifier=globalVariable.getNameOfTheVariable(); //Si preleva l'identifier
        Type type=globalVariable.getType(); //Si preleva il tipo
        //Si verifica che non sia gia stata definita una variabile con stesso nome
        if(symbolTable.get(identifier)!=null){ //if it's already defined then you need to throw an Exception
            throw new ScopeError("GlobalVariable "+identifier +" already defined "+ "(line "+globalVariable.getLine()+")");
        }
        symbolTable.add(identifier,new SymbolTableType(type));
    }


    @Override
    public void visit(Procedure procedure, SymbolTable symbolTable,SymbolTable structTable) throws SemanticException {
        Type return_type=procedure.getReturnType(); //Si preleva il tipo
        String procedure_identifier=procedure.getProcedureName();
        ArrayList<VariableDeclaration> procedure_parameters=procedure.getParameters_of_the_procedure();
        //Io prendo la entry delle procedure con lo stesso nome stesso return type ma diversi parametri
        SymbolTableProceduresEntry procedures=(SymbolTableProceduresEntry) symbolTable.get(procedure_identifier);
        //TODO POTREBBE RITORNARE UNA COSTANTE CHE HA LO STESSO NOME
        if(procedures!=null){
            //Procedure have been already defined
            //Ok we have two procedure with two same name
            //but the list of parameter need to be different
            if(procedures.containsAProcedureWithParameters(procedure_parameters)){
                throw new ScopeError("You have already define a procedure with the same name and same parameters"+
                        "(line: "+procedure.getLine()+")");
            }
            //ok not exist the same procedure we need to add
            procedures.addFunction(procedure_parameters,new SymbolTableType(return_type));
            return;
        }
        procedures=new SemanticProceduresEntry("procedures");
        procedures.addFunction(procedure_parameters,new SemanticType(return_type.getSymbol().getValue()));
        symbolTable.add(procedure_identifier,procedures);

    }



    @Override
    public void visit(Block block, SymbolTable symbolTable,SymbolTable structTable) {

    }

    @Override
    public void visit(ASTNode statement, SymbolTable symbolTable, SymbolTable structTable) throws SemanticException {

    }

    @Override
    public void visit(FunctionCall functionCall, SymbolTable symbolTable, SymbolTable structTable) throws SemanticException {

    }

    @Override
    public void visit(IfStatement ifStatement, SymbolTable symbolTable,SymbolTable structTable) {

    }

    @Override
    public void visit(IfElseStatement ifElseStatement, SymbolTable symbolTable, SymbolTable structTable) throws SemanticException {

    }

    @Override
    public void visit(ForStatement forStatement, SymbolTable symbolTable,SymbolTable structTable) {

    }

    @Override
    public void visit(WhileStatement whileStatement, SymbolTable symbolTable,SymbolTable structTable) {

    }

    @Override
    public void visit(VariableDeclaration variableDeclaration, SymbolTable symbolTable,SymbolTable structTable) {

    }

    @Override
    public void visit(VariableAssigment variableAssigment, SymbolTable symbolTable,SymbolTable structTable) {

    }

    @Override
    public void visit(ReturnStatement returnStatement,SymbolTable symbolTable,SymbolTable structTable){

    }

    @Override
    public void visit(VariableInstantiation variableInstantiation, SymbolTable symbolTable, SymbolTable structTable) throws SemanticException {

    }
}
