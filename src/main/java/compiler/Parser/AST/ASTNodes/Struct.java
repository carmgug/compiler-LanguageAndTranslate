package compiler.Parser.AST.ASTNodes;

import compiler.Lexer.Symbol;

import java.util.ArrayList;

public class Struct {

    Symbol identifier;
    ArrayList<Field> fields;

    public Struct(Symbol identifier, ArrayList<Field> fields){
        this.identifier=identifier;
        this.fields=fields;
    }

    public Struct(){
        this.identifier=null;
        this.fields=null;
    }






}
