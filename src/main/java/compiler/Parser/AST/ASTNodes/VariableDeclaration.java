package compiler.Parser.AST.ASTNodes;

import compiler.Exceptions.SemanticException.SemanticErrorException;
import compiler.Lexer.Symbol;
import compiler.Parser.AST.ASTNode;
import compiler.Parser.AST.ASTNodes.Expressions.Type;
import compiler.Parser.AST.ASTNodes.Expressions.VariableReference;
import compiler.SemanticAnalysis.SymbolTable;
import compiler.SemanticAnalysis.Visitor.Visitor;

public class VariableDeclaration extends ASTNode {

    protected Type type;
    protected VariableReference variable;

    public VariableDeclaration(Type type, VariableReference identifier){
        this.type=type;
        this.variable=identifier;
    }


    public Type getType(){
        return this.type;
    }

    public VariableReference getIdentifier(){
        return this.variable;
    }

    public String toString(){
        return "VariableDeclaration: {"+
                "Type: " + type + ","+
                "Identifier: " + variable.getIdentifier() +
                "}";
    }

    @Override
    public void accept(Visitor visitor, SymbolTable symbolTable,SymbolTable structTable) throws SemanticErrorException {
        visitor.visit(this,symbolTable);
    }
}
