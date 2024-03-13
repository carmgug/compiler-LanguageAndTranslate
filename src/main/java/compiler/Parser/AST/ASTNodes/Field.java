package compiler.Parser.AST.ASTNodes;

import compiler.Lexer.Symbol;
import compiler.Parser.AST.ASTNodes.Expressions.Type;

public class Field {

    private Type type;
    private Symbol identifier;

    public Field(Type type, Symbol identifier){
        this.type=type;
        this.identifier=identifier;
    }

    public Field(){
        this.type=null;
        this.identifier=null;
    }

    public Type getType(){
        return this.type;
    }

    public Symbol getIdentifier(){
        return this.identifier;
    }

    public String toString(){
        return "Type: "+this.type.toString()+" Identifier: "+this.identifier.getValue();
    }
}
