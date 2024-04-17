package compiler.Parser.AST.ASTNodes.Expressions;

import compiler.Exceptions.SemanticException.SemanticErrorException;
import compiler.Parser.AST.ASTNodes.ExpressionStatement;
import compiler.SemanticAnalysis.SymbolTable;
import compiler.SemanticAnalysis.Visitor.Visitor;

public class ArrayInitialization extends ExpressionStatement {

    Type type;
    ExpressionStatement size;
    public ArrayInitialization(Type type, ExpressionStatement size) {
        this.type = type;
        this.size = size;
    }

    public String toString() {
        return "ArrayInitialization {" +
                    "type: " + type.toString() +","+
                    "size: " + size.toString()+
                "}";
    }

    public Type getType(){return this.type;}

    public ExpressionStatement getSize(){return this.size;}


    @Override
    public void accept(Visitor visitor, SymbolTable symbolTable,SymbolTable structTable) throws SemanticErrorException {
        visitor.visit(this,symbolTable,structTable);
    }
}
