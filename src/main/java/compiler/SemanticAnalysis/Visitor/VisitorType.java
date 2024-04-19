package compiler.SemanticAnalysis.Visitor;

import compiler.Exceptions.SemanticException.SemanticErrorException;
import compiler.Parser.AST.ASTNodes.*;
import compiler.Parser.AST.ASTNodes.Expressions.*;
import compiler.Parser.AST.ASTNodes.Expressions.NegationNodes.ArithmeticNegationNode;
import compiler.Parser.AST.ASTNodes.Expressions.NegationNodes.BooleanNegationNode;
import compiler.SemanticAnalysis.SymbolTable.SymbolTable;

public interface VisitorType {
    Type visit(Constant constant, SymbolTable symbolTable, SymbolTable structTable) throws SemanticErrorException;

    Type visit(ArrayAccess arrayAccess, SymbolTable symbolTable, SymbolTable structTable) throws SemanticErrorException;

    Type visit(ExpressionStatement expressionStatement, SymbolTable symbolTable, SymbolTable structTable) throws SemanticErrorException;

    Type visit(BinaryExpression binaryExpression, SymbolTable symbolTable, SymbolTable structTable) throws SemanticErrorException;
    Type visit(Operator operator, Type leftType, Type rightType) throws SemanticErrorException;

    Type visit(Value value, SymbolTable symbolTable, SymbolTable structTable);
    Type visit(VariableReference variableReference, SymbolTable symbolTable, SymbolTable structTable) throws SemanticErrorException;

    Type visit(StructAccess structAccess, SymbolTable symbolTable, SymbolTable structTable) throws SemanticErrorException;
    Type visit(ArithmeticNegationNode arithmeticNegationNode, SymbolTable symbolTable, SymbolTable structTable) throws SemanticErrorException;
    Type visit(BooleanNegationNode booleanNegationNode, SymbolTable symbolTable, SymbolTable structTable) throws SemanticErrorException;

    Type visit(FunctionCall functionCall, SymbolTable symbolTable, SymbolTable structTable) throws SemanticErrorException;
    Type visit(ArrayValueDeclaration arrayValueDeclaration, SymbolTable symbolTable, SymbolTable structTable) throws SemanticErrorException;
    Type visit(ArrayInitialization arrayInitialization, SymbolTable symbolTable, SymbolTable structTable) throws SemanticErrorException;
}

