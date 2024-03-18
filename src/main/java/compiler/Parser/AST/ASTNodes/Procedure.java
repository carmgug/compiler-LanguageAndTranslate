package compiler.Parser.AST.ASTNodes;

import compiler.Lexer.SpeciefiedSymbol.BaseType;
import compiler.Lexer.SpeciefiedSymbol.Identifier;
import compiler.Parser.AST.ASTNodes.Expressions.Type;

import java.util.ArrayList;

public class Procedure {
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

    public Procedure(Type returnType, Symbol name,ArrayList<VariableDeclaration> parameters_of_the_procedure ,Block body){
        this.returnType=returnType;
        this.name=name;
        this.parameters_of_the_procedure=parameters_of_the_procedure;
        this.body=body;
    }

    public String toString(){
        return "{Procedure: "+this.name.getValue()+"\nReturn Type: "+this.returnType.toString()+"\nParameters: "+this.parameters_of_the_procedure.toString()+"\nBody: "+this.body.toString();
    }








}
