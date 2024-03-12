package compiler.Parser.AST.ASTNodes;

import compiler.Lexer.Symbol;

public class Field {

    private Symbol type;
    private Symbol identifier;

    public Field(Symbol type, Symbol identifier){
        this.type=type;
        this.identifier=identifier;
    }
}
