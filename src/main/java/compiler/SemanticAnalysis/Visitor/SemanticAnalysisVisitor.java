package compiler.SemanticAnalysis.Visitor;

import compiler.Exceptions.SemanticException.SemanticErrorException;
import compiler.Lexer.Symbol;
import compiler.Lexer.Token;
import compiler.Parser.AST.ASTNode;
import compiler.Parser.AST.ASTNodes.*;
import compiler.Parser.AST.ASTNodes.Expressions.Type;
import compiler.Parser.AST.ASTNodes.Expressions.Types.BaseType;
import compiler.Parser.AST.ASTNodes.Expressions.Types.StructType;

import compiler.SemanticAnalysis.SymbolTable.SymbolTable;
import compiler.SemanticAnalysis.SymbolTable.SymbolTableValues.SemanticStructType;
import compiler.SemanticAnalysis.SymbolTable.SymbolTableValues.SymbolTableType;

import java.util.ArrayList;

public class SemanticAnalysisVisitor implements Visitor{


    TypeCeckingVisitor typeCeckingVisitor=new TypeCeckingVisitor();

    @Override
    public void visit(Constant constant, SymbolTable symbolTable,SymbolTable structTable) throws SemanticErrorException {
        //We are performing a semanticAnalysis on a constant
        //The costant is already in the symboltTable
        //Buw we need to check if the right part is the same type as the left part declared
        Type expected_type=constant.getType();
        ExpressionStatement exp=constant.getRight_side();
        Type observed_type= typeCeckingVisitor.visit(exp,symbolTable,structTable);

        //Un float può essere un int, ma un int non può essere un float
        if(expected_type.getSymbol().getType().equals(Token.FloatType) && observed_type.getSymbol().getType().equals(Token.IntType)){
            return;
        }
        if(!expected_type.equals(observed_type)){
            throw new SemanticErrorException("Type of the constant '"+constant.getIdentifier().getValue()+"' at line "+constant.getIdentifier().getLine()+" is not the same as the type of the right side of the assignment " +
                    "Expected type: "+expected_type+" Observed type: "+observed_type+")");
        }
    }

    @Override
    public void visit(Struct struct,SymbolTable symbolTable,SymbolTable structTable) throws SemanticErrorException {
        //We are performing a semanticAnalysis on a struct
        //The struct is already in the symboltTable
        //We need to check if the struct is already defined
        String structName= struct.getIdentifier().getValue();
        if(structTable.get(structName)==null){ //if the struct is not defined we throw an exception
            throw new SemanticErrorException("Struct "+structName +" is not defined"+ "(line "+struct.getIdentifier().getLine()+")");
        }
        SemanticStructType structEntry=new SemanticStructType(new StructType(struct.getIdentifier()));
        for(VariableDeclaration var: struct.getVariableDeclarations()){
            Type var_type=var.getType();
            //if the type is a struct or array of struct we need to check if the struct is already defined
            if(var_type instanceof StructType){ //include anche ArrayStructType perchè è un estensione di StructType
                if(structTable.get(var_type.getSymbol().getValue())==null){
                    throw new SemanticErrorException("Struct "+var_type.getSymbol().getValue() +" not defined "+ "(line "+var_type.getSymbol().getLine()+")");
                }
                structEntry.addField(var.getIdentifier().getIdentifier(),new SymbolTableType(var_type));
            }
            //if the type is a array of struct we need to check if the struct is already defined
            else {//Parliamo di base type o array di base type quindi aggiungiamo
                structEntry.addField(var.getIdentifier().getIdentifier(),new SymbolTableType(var_type));
            }
        }

        structTable.add(structName,structEntry);

    }

    @Override
    public void visit(GlobalVariable globalVariable, SymbolTable symbolTable,SymbolTable structTable) throws SemanticErrorException {
        Type expected_type=globalVariable.getType();
        ExpressionStatement exp=globalVariable.getValue();
        Type observed_type= typeCeckingVisitor.visit(exp,symbolTable,structTable);
        //Un float può essere un int, ma un int non può essere un float
        if(expected_type.getSymbol().getType().equals(Token.FloatType) && observed_type.getSymbol().getType().equals(Token.IntType)){
            return;
        }
        if(!expected_type.equals(observed_type)){
            throw new SemanticErrorException("Type of the constant '"+globalVariable.getIdentifier().getValue()+"' at line "+globalVariable.getIdentifier().getLine()+" is not the same as the type of the right side of the assignment " +
                    "Expected type: "+expected_type+" Observed type: "+observed_type+")");
        }

    }
    @Override
    public void visit(Procedure procedure, SymbolTable symbolTable,SymbolTable structTable) throws SemanticErrorException {
        //A procedure is a definition of a function but we have already added to symbolTable in previous phase, if it's
        //a call of a procedure we have FunctionCall (But we are here so we dont need to check the existance of the procedure but the block);
        //and the return;

        String procedure_identifier= procedure.getProcedureName();
        ArrayList<VariableDeclaration> parameters=procedure.getParameters_of_the_procedure();

        Type expected_type=procedure.getReturnType();
        if(symbolTable.get(procedure_identifier)==null){
            throw new SemanticErrorException("Procedure "+procedure_identifier +" used but has not been defiened "+ "(line "+procedure.getName().getLine()+")");
        }
        //Dobbiamo fare il check del body
        //Dobbiamo creare una nuova symbol table
        SymbolTable procedureSymbolTable=new SymbolTable(symbolTable);
        for(VariableDeclaration parameter:parameters){
            Type parameter_type=parameter.getType();
            procedureSymbolTable.add(parameter.getNameOfVariable(),new SymbolTableType(parameter_type));
        }
        procedureSymbolTable.add("return",new SymbolTableType(expected_type));
        //Dobbiamo visitare il body
        this.visit(procedure.getBody(),procedureSymbolTable,structTable);

    }



    @Override
    public void visit(Block block, SymbolTable symbolTable, SymbolTable structTable) throws SemanticErrorException {
        //We are performing a semanticAnalysis on a block
        //We need to perform semantic analysis on all of his statement and if we encounter a return
        //we need to check if the return type is the same as the expected return type


        for(ASTNode statement:block.getStatements()){
            //if we are talking about ReturnStatement, IfBlock, IfElseBlock, ForBlock, WhileBlock we need to check the return type
            visit(statement,symbolTable,structTable);
        }

    }

    @Override
    public void visit(ASTNode statement, SymbolTable symbolTable, SymbolTable structTable) throws SemanticErrorException {
        statement.accept(this,symbolTable,structTable);
    }

    @Override
    public void visit(IfStatement ifStatement, SymbolTable symbolTable,SymbolTable structTable) throws SemanticErrorException {
        //We are performing a semanticAnalysis on a ifStatement

        ExpressionStatement condition=ifStatement.getIfCondition();
        Type condition_type=typeCeckingVisitor.visit(condition,symbolTable,structTable);
        if(!condition_type.getSymbol().getType().equals(Token.BoolType)){
            throw new SemanticErrorException("The condition of the if statement is not a boolean (line ");
        }
        SymbolTable ifBlockSymbolTable=new SymbolTable(symbolTable);
        visit(ifStatement.getIfBlock(),ifBlockSymbolTable,structTable);
    }

    @Override
    public void visit(IfElseStatement ifElseStatement, SymbolTable symbolTable, SymbolTable structTable) throws SemanticErrorException{
        //We are performing a semanticAnalysis on a ifElseStatement
        ExpressionStatement condition=ifElseStatement.getIfCondition();
        Type condition_type=typeCeckingVisitor.visit(condition,symbolTable,structTable);
        if(!condition_type.getSymbol().getType().equals(Token.BoolType)){
            throw new SemanticErrorException("The condition of the else if statement is not a boolean (line ");
        }
        SymbolTable ifBlockSymbolTable=new SymbolTable(symbolTable);
        visit(ifElseStatement.getIfBlock(),ifBlockSymbolTable,structTable);
        SymbolTable elseBlockSymbolTable=new SymbolTable(symbolTable);
        visit(ifElseStatement.getElse_block(),elseBlockSymbolTable,structTable);
    }

    @Override
    public void visit(ForStatement forStatement, SymbolTable symbolTable,SymbolTable structTable) throws SemanticErrorException {
        //ForStatement()

        //VariableAssigment start|null,ExpressionStatement endCondition|null, VariableAssigment update|null, Block block
        VariableAssigment start=forStatement.getStart();
        if(start!=null) visit(start,symbolTable,structTable);
        ExpressionStatement endCondition= forStatement.getEndCondition();
        Type ob_type_endCondition= endCondition!=null ?
                typeCeckingVisitor.visit(endCondition,symbolTable,structTable) :
                new BaseType(new Symbol(Token.BoolType, "true"));
        if(!ob_type_endCondition.getSymbol().getType().equals(Token.BoolType)){
            throw new SemanticErrorException("The condition of the for statement is not a boolean (line "+endCondition+")");
        }
        VariableAssigment update=forStatement.getUpdate();
        if(update!=null) visit(update,symbolTable,structTable);
        //Ok then visit the block
        SymbolTable forBlockSymbolTable=new SymbolTable(symbolTable);
        visit(forStatement.getBlock(),forBlockSymbolTable,structTable);
    }

    @Override
    public void visit(WhileStatement whileStatement, SymbolTable symbolTable,SymbolTable structTable) throws SemanticErrorException {
        //WhileStatement (condition1)
        //TODO: Manage the case when condition1 is missing so the while is infinite Maybe
        Type observed_type_of_the_exit_condition=typeCeckingVisitor.visit(whileStatement.getExitCondition(),symbolTable,structTable);
        if(!observed_type_of_the_exit_condition.getSymbol().getType().equals(Token.BoolType)){
            throw new SemanticErrorException("The condition of the while statement is not a boolean (line ");
        }
        SymbolTable whileBlockSymbolTable=new SymbolTable(symbolTable);
        visit(whileStatement.getBlock(),whileBlockSymbolTable,structTable);
    }

    @Override
    public void visit(VariableDeclaration variableDeclaration, SymbolTable symbolTable,SymbolTable structTable) throws SemanticErrorException {
        //Type NameOfTheVariable;
        String identifier = variableDeclaration.getNameOfVariable();
        if(symbolTable.containsInCurrentScope(identifier)){
            throw new SemanticErrorException(identifier+" already contained in the current symbol Table ");
        }
        symbolTable.add(identifier,new SymbolTableType(variableDeclaration.getType()));
    }

    @Override
    public void visit(VariableAssigment variableAssigment, SymbolTable symbolTable,SymbolTable structTable) throws SemanticErrorException {
        ExpressionStatement left_part= variableAssigment.getVariable();
        ExpressionStatement right_part= variableAssigment.getRight_side();
        Type left_part_type=typeCeckingVisitor.visit(left_part,symbolTable,structTable);
        Type right_part_type=typeCeckingVisitor.visit(right_part,symbolTable,structTable);
        if(!left_part_type.equals(right_part_type)){
            throw new SemanticErrorException("Type of the left part of the assignment is not the same of the type of the right part of the assignment " +
                    "Expected type: "+left_part_type+" Observed type: "+right_part_type+")");
        }
    }


    @Override
    public void visit(ReturnStatement returnStatement, SymbolTable symbolTable,SymbolTable structTable) throws SemanticErrorException {
        Type observed_type= typeCeckingVisitor.visit(returnStatement.getExpression(),symbolTable,structTable);
        Type expected_type= ((SymbolTableType)symbolTable.get("return")).getType();

        if(expected_type==null || !expected_type.equals(observed_type)){
            throw new SemanticErrorException("Type of the return statement  is not the same of the type of the curr Procedure " +
                    "Expected type: "+expected_type+" Observed type: "+observed_type+")");
        }
    }


}
