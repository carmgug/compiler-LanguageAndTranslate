package compiler.Parser.AST.ASTNodes;

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

    public Symbol getIdentifier(){
        return this.identifier;
    }

    public String toString(){
        return "Struct: {"+
                "Identifier: " + identifier.getValue() + ","+
                "declaredVariables: "+variableDeclarations +
                "}";
    }






}
