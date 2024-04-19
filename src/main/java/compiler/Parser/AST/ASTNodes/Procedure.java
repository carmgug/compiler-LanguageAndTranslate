package compiler.Parser.AST.ASTNodes;


import compiler.Exceptions.SemanticException.SemanticException;
import compiler.Lexer.Symbol;
import compiler.Lexer.Token;
import compiler.Parser.AST.ASTNode;
import compiler.Parser.AST.ASTNodes.Expressions.Type;
import compiler.SemanticAnalysis.SymbolTable.SymbolTable;
import compiler.SemanticAnalysis.Visitor.Visitor;
import compiler.SemanticAnalysis.Visitor.VisitorType;

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
    public void accept(Visitor visitor, SymbolTable symbolTable,SymbolTable structTable) throws SemanticException {
        visitor.visit(this,symbolTable,structTable);
    }

    @Override
    public Type accept(VisitorType visitor, SymbolTable symbolTable, SymbolTable structTable) throws SemanticException {
        throw new SemanticException("Sould not run");
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
    public String getProcedureName(){return this.name.getValue();}
    /*
        @return the line number where the procedure was declared
     */
    public int getLine(){return this.name.getLine();}

    public ArrayList<VariableDeclaration> getParameters_of_the_procedure()
    {return this.parameters_of_the_procedure;}

    public Block getBody(){return this.body;}







}
