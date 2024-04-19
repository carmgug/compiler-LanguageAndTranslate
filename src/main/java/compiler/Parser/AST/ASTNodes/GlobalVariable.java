package compiler.Parser.AST.ASTNodes;

import compiler.Exceptions.SemanticException.SemanticErrorException;
import compiler.Lexer.Symbol;
import compiler.Parser.AST.ASTNode;
import compiler.Parser.AST.ASTNodes.Expressions.Type;
import compiler.SemanticAnalysis.SymbolTable.SymbolTable;
import compiler.SemanticAnalysis.Visitor.Visitor;
import compiler.SemanticAnalysis.Visitor.VisitorType;

public class GlobalVariable extends ASTNode {

    private Type type;

    private Symbol identifier;

    private ExpressionStatement value;

    public GlobalVariable(Type type, Symbol identifier, ExpressionStatement value) {
        this.type = type;
        this.identifier = identifier;
        this.value = value;
    }

    public String toString() {
        return "GlobalVariable: {"+
                "Type: " + type + ","+
                "Identifier: " + identifier.getValue() + ","+
                "Value: "+value +
                "}";
    }

    public Symbol getIdentifier(){
        return this.identifier;
    }

    public Type getType(){
        return this.type;
    }

    public ExpressionStatement getValue(){
        return this.value;
    }


    @Override
    public void accept(Visitor visitor, SymbolTable symbolTable,SymbolTable structTable) throws SemanticErrorException {
        visitor.visit(this,symbolTable,structTable);
    }

    @Override
    public Type accept(VisitorType visitor, SymbolTable symbolTable, SymbolTable structTable) throws SemanticErrorException {
        throw new SemanticErrorException("Sould not run");
    }
}
