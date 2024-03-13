package compiler.Parser.AST.ASTNodes;

import compiler.Lexer.Symbol;

public class Field {

    private Symbol type;
    private Symbol identifier;

    public Field(Symbol type, Symbol identifier){
        this.type=type;
        this.identifier=identifier;
    }

    public Field(){
        this.type=null;
        this.identifier=null;
    }

    public Symbol getType(){
        return this.type;
    }

    public Symbol getIdentifier(){
        return this.identifier;
    }

    public String toString(){
        return "Type: "+this.type.getValue()+" Identifier: "+this.identifier.getValue();
    }
}
