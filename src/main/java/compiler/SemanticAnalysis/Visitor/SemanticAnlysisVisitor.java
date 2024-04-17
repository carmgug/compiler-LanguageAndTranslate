package compiler.SemanticAnalysis.Visitor;

import compiler.Exceptions.SemanticException.SemanticErrorException;
import compiler.Parser.AST.ASTNodes.*;
import compiler.SemanticAnalysis.SymbolTable;

public class SemanticAnlysisVisitor implements Visitor{

    @Override
    public void visit(Constant constant, SymbolTable symbolTable,SymbolTable structTable) throws SemanticErrorException {

    }

    @Override
    public void visit(Struct struct,SymbolTable symbolTable,SymbolTable structTable) throws SemanticErrorException {

    }

    @Override
    public void visit(GlobalVariable globalVariable, SymbolTable symbolTable,SymbolTable structTable) throws SemanticErrorException {

    }

    @Override
    public void visit(ExpressionStatement expressionStatement, SymbolTable symbolTable,SymbolTable structTable) throws SemanticErrorException {

    }

    @Override
    public void visit(Procedure procedure, SymbolTable symbolTable,SymbolTable structTable) throws SemanticErrorException {

    }

    @Override
    public void visit(Block block, SymbolTable symbolTable,SymbolTable structTable) {

    }

    @Override
    public void visit(IfStatement ifStatement, SymbolTable symbolTable,SymbolTable structTable) {

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
    public void visit(ReturnStatement returnStatement, SymbolTable symbolTable,SymbolTable structTable) {

    }
}
