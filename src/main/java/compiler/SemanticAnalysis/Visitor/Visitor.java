package compiler.SemanticAnalysis.Visitor;

import compiler.Exceptions.SemanticException.SemanticErrorException;
import compiler.Parser.AST.ASTNodes.*;
import compiler.SemanticAnalysis.SymbolTable;



/*
    The Visitor interface: This interface must declare a method for each type of element in the data structure that
    you want to visit.
 */

public interface Visitor {
    void visit(Constant constant,SymbolTable symbolTable) throws SemanticErrorException;
    void visit(Struct struct,SymbolTable symbolTable,SymbolTable structTable) throws SemanticErrorException;
    void visit(GlobalVariable globalVariable,SymbolTable symbolTable,SymbolTable structTable) throws SemanticErrorException;
    void visit(ExpressionStatement expressionStatement, SymbolTable symbolTable,SymbolTable structTable) throws SemanticErrorException;
    void visit(Procedure procedure,SymbolTable symbolTable,SymbolTable structTable) throws SemanticErrorException;
    void visit(Block block,SymbolTable symbolTable,SymbolTable structTable);
    void visit(IfStatement ifStatement, SymbolTable symbolTable,SymbolTable structTable);
    void visit(ForStatement forStatement,SymbolTable symbolTable,SymbolTable structTable);
    void visit(WhileStatement whileStatement, SymbolTable symbolTable,SymbolTable structTable);
    void visit(VariableDeclaration variableDeclaration, SymbolTable symbolTable,SymbolTable structTable);
    void visit(VariableAssigment variableAssigment, SymbolTable symbolTable,SymbolTable structTable);
    void visit(ReturnStatement returnStatement,SymbolTable symbolTable,SymbolTable structTable);
}