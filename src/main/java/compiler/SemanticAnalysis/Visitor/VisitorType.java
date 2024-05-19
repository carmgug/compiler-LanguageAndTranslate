package compiler.SemanticAnalysis.Visitor;

import compiler.Exceptions.SemanticException.SemanticException;
import compiler.Parser.AST.ASTNodes.*;
import compiler.Parser.AST.ASTNodes.Expressions.*;
import compiler.Parser.AST.ASTNodes.Expressions.NegationNodes.ArithmeticNegationNode;
import compiler.Parser.AST.ASTNodes.Expressions.NegationNodes.BooleanNegationNode;
import compiler.SemanticAnalysis.SymbolTable.SymbolTable;

public interface VisitorType {
    Type visit(Constant constant, SymbolTable symbolTable, SymbolTable structTable) throws SemanticException;

    Type visit(ArrayAccess arrayAccess, SymbolTable symbolTable, SymbolTable structTable) throws SemanticException;

    Type visit(ExpressionStatement expressionStatement, SymbolTable symbolTable, SymbolTable structTable) throws SemanticException;

    Type visit(BinaryExpression binaryExpression, SymbolTable symbolTable, SymbolTable structTable) throws SemanticException;
    Type visit(Operator operator, Type leftType, Type rightType) throws SemanticException;

    Type visit(Value value, SymbolTable symbolTable, SymbolTable structTable);
    Type visit(VariableReference variableReference, SymbolTable symbolTable, SymbolTable structTable) throws SemanticException;

    Type visit(StructAccess structAccess, SymbolTable symbolTable, SymbolTable structTable) throws SemanticException;
    Type visit(ArithmeticNegationNode arithmeticNegationNode, SymbolTable symbolTable, SymbolTable structTable) throws SemanticException;
    Type visit(BooleanNegationNode booleanNegationNode, SymbolTable symbolTable, SymbolTable structTable) throws SemanticException;

    Type visit(FunctionCall functionCall, SymbolTable symbolTable, SymbolTable structTable) throws SemanticException;
    Type visit(ArrayValueDeclaration arrayValueDeclaration, SymbolTable symbolTable, SymbolTable structTable) throws SemanticException;
    Type visit(ArrayInitialization arrayInitialization, SymbolTable symbolTable, SymbolTable structTable) throws SemanticException;
}

