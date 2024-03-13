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

    public Symbol getIdentifier(){
        return this.identifier;
    }

    public String toString(){
        String ret="\n Struct "+identifier.getValue()+"{\n";
        for(int i=0;i<fields.size();i++){
            Field field= fields.get(i);
            ret+="\tField{"+field.toString()+" }\n";
        }
        ret+=" }";

        return ret;
    }






}
