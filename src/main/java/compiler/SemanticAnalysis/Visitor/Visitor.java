package compiler.SemanticAnalysis.Visitor;

import compiler.Exceptions.SemanticException.SemanticErrorException;
import compiler.Parser.AST.ASTNode;
import compiler.Parser.AST.ASTNodes.*;
import compiler.Parser.AST.ASTNodes.Expressions.FunctionCall;
import compiler.Parser.AST.ASTNodes.Expressions.Type;
import compiler.SemanticAnalysis.SymbolTable.SymbolTable;



/*
    The Visitor interface: This interface must declare a method for each type of element in the data structure that
    you want to visit.
 */

public interface Visitor {
    void visit(Constant constant,SymbolTable symbolTable,SymbolTable structTable) throws SemanticErrorException;
    void visit(Struct struct,SymbolTable symbolTable,SymbolTable structTable) throws SemanticErrorException;
    void visit(GlobalVariable globalVariable,SymbolTable symbolTable,SymbolTable structTable) throws SemanticErrorException;

    void visit(Procedure procedure,SymbolTable symbolTable,SymbolTable structTable) throws SemanticErrorException;

    void visit(Block block, SymbolTable symbolTable, SymbolTable structTable) throws SemanticErrorException;

    void visit(ASTNode statement, SymbolTable symbolTable, SymbolTable structTable) throws SemanticErrorException;

    void visit(FunctionCall functionCall, SymbolTable symbolTable, SymbolTable structTable) throws SemanticErrorException;

    void visit(IfStatement ifStatement, SymbolTable symbolTable, SymbolTable structTable) throws SemanticErrorException;

    void visit(IfElseStatement ifElseStatement, SymbolTable symbolTable, SymbolTable structTable) throws SemanticErrorException;

    void visit(ForStatement forStatement, SymbolTable symbolTable, SymbolTable structTable) throws SemanticErrorException;
    void visit(WhileStatement whileStatement, SymbolTable symbolTable,SymbolTable structTable) throws SemanticErrorException;
    void visit(VariableDeclaration variableDeclaration, SymbolTable symbolTable,SymbolTable structTable) throws SemanticErrorException;
    void visit(VariableAssigment variableAssigment, SymbolTable symbolTable,SymbolTable structTable) throws SemanticErrorException;
    void visit(ReturnStatement returnStatement,SymbolTable symbolTable,SymbolTable structTable) throws SemanticErrorException;


}