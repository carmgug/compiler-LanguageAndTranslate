package compiler.Parser.AST.ASTNodes;

import compiler.Exceptions.SemanticException.SemanticErrorException;
import compiler.Lexer.Symbol;
import compiler.Parser.AST.ASTNode;

import java.util.ArrayList;

public class Struct extends ASTNode {

    Symbol identifier;
    ArrayList<VariableDeclaration> variableDeclarations;

    public Struct(Symbol identifier, ArrayList<VariableDeclaration> variableDeclarations){
        this.identifier=identifier;
        this.variableDeclarations = variableDeclarations;
    }

    public Struct(){
        this.identifier=null;
        this.variableDeclarations =null;
    }

    @Override
    public void accept(Visitor visitor, SymbolTable symbolTable,SymbolTable structTable) throws SemanticErrorException {
        visitor.visit(this,symbolTable,structTable);

    }

    public Symbol getIdentifier(){
        return this.identifier;
    }

    public String toString(){
        return "Struct: {"+
                "Identifier: " + identifier.getValue() + ","+
                "declaredVariables: "+variableDeclarations +
                "}";
    }

    public ArrayList<VariableDeclaration> getVariableDeclarations(){
        return this.variableDeclarations;
    }






}
