package compiler.SemanticAnalysis.Visitor;

import compiler.Exceptions.SemanticException.SemanticException;
import compiler.Parser.AST.ASTNode;
import compiler.Parser.AST.ASTNodes.*;
import compiler.Parser.AST.ASTNodes.Expressions.FunctionCall;
import compiler.SemanticAnalysis.SymbolTable.SymbolTable;



/*
    The Visitor interface: This interface must declare a method for each type of element in the data structure that
    you want to visit.
 */

public interface Visitor {
    void visit(Constant constant,SymbolTable symbolTable,SymbolTable structTable) throws SemanticException;
    void visit(Struct struct,SymbolTable symbolTable,SymbolTable structTable) throws SemanticException;
    void visit(GlobalVariable globalVariable,SymbolTable symbolTable,SymbolTable structTable) throws SemanticException;

    void visit(Procedure procedure,SymbolTable symbolTable,SymbolTable structTable) throws SemanticException;

    void visit(Block block, SymbolTable symbolTable, SymbolTable structTable) throws SemanticException;

    void visit(ASTNode statement, SymbolTable symbolTable, SymbolTable structTable) throws SemanticException;

    void visit(FunctionCall functionCall, SymbolTable symbolTable, SymbolTable structTable) throws SemanticException;

    void visit(IfStatement ifStatement, SymbolTable symbolTable, SymbolTable structTable) throws SemanticException;

    void visit(IfElseStatement ifElseStatement, SymbolTable symbolTable, SymbolTable structTable) throws SemanticException;

    void visit(ForStatement forStatement, SymbolTable symbolTable, SymbolTable structTable) throws SemanticException;
    void visit(WhileStatement whileStatement, SymbolTable symbolTable,SymbolTable structTable) throws SemanticException;
    void visit(VariableDeclaration variableDeclaration, SymbolTable symbolTable,SymbolTable structTable) throws SemanticException;
    void visit(VariableAssigment variableAssigment, SymbolTable symbolTable,SymbolTable structTable) throws SemanticException;
    void visit(ReturnStatement returnStatement,SymbolTable symbolTable,SymbolTable structTable) throws SemanticException;
    void visit(VariableInstantiation variableInstantiation,SymbolTable symbolTable,SymbolTable structTable) throws SemanticException;

}