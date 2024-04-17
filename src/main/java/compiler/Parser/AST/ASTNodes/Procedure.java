package compiler.Parser.AST.ASTNodes;


import compiler.Exceptions.SemanticException.SemanticErrorException;
import compiler.Lexer.Symbol;
import compiler.Lexer.Token;
import compiler.Parser.AST.ASTNode;
import compiler.Parser.AST.ASTNodes.Expressions.Type;
import compiler.SemanticAnalysis.SymbolTable;
import compiler.SemanticAnalysis.Visitor.Visitor;

import java.util.ArrayList;

public class Procedure extends ASTNode {
    Type returnType;
    Symbol name;
    ArrayList<VariableDeclaration> parameters_of_the_procedure;
    Block body;


    public Procedure(){
        this.returnType=null;
        this.name=null;
        this.parameters_of_the_procedure=null;
        this.body=null;
    }

    @Override
    public void accept(Visitor visitor, SymbolTable symbolTable,SymbolTable structTable) throws SemanticErrorException {
        visitor.visit(this,symbolTable,structTable);
    }

    public Procedure(Type returnType, Symbol name,ArrayList<VariableDeclaration> parameters_of_the_procedure ,Block body){
        if(!name.getType().equals(Token.Identifier)) throw new IllegalArgumentException("Procedure name must be an identifier");
        this.returnType=returnType;
        this.name=name;
        this.parameters_of_the_procedure=parameters_of_the_procedure;
        this.body=body;
    }

    public String toString(){
        return "Procedure: {"+
                "Return Type: " + returnType + ","+
                "Name: " + name.getValue() + ","+
                "Parameters: "+(parameters_of_the_procedure.size()==0 ? "None" : parameters_of_the_procedure) + ","+
                "Body: "+body +
                "}";
    }

    public Type getReturnType(){return this.returnType;}

    public Symbol getName(){return this.name;}

    public ArrayList<VariableDeclaration> getParameters_of_the_procedure()
    {return this.parameters_of_the_procedure;}

    public Block getBody(){return this.body;}








}
