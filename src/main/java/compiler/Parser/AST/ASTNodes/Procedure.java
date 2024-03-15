package compiler.Parser.AST.ASTNodes;

import compiler.Lexer.SpeciefiedSymbol.BaseType;
import compiler.Lexer.SpeciefiedSymbol.Identifier;
import compiler.Parser.AST.ASTNodes.Expressions.Type;

import java.util.ArrayList;

public class Procedure {
    Type returnType;
    Identifier name;
    ArrayList<Parameter> parameters;
    Block body;

    public Procedure(){
        this.returnType=null;
        this.name=null;
        this.parameters=null;
        this.body=null;
    }

    public Procedure(Type returnType, Identifier name,ArrayList<Parameter> parameters ,Block body){
        this.returnType=returnType;
        this.name=name;
        this.parameters=parameters;
        this.body=body;
    }








}
