package compiler.SemanticAnalysis.Visitor;


import compiler.Exceptions.SemanticException.SemanticErrorException;
import compiler.Parser.AST.ASTNodes.Expressions.Type;
import compiler.SemanticAnalysis.SymbolTable.SymbolTable;

/*
    The visitable elements interface: This interface defines an accept method that takes the visitor as an argument.
    Each visitable element class will implement this interface.
 */
public interface Visitable {

    void accept(Visitor visitor, SymbolTable symbolTable,SymbolTable structTable) throws SemanticErrorException;
    Type accept(VisitorType visitor, SymbolTable symbolTable, SymbolTable structTable) throws SemanticErrorException;


}
