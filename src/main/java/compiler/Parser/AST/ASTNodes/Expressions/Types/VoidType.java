package compiler.Parser.AST.ASTNodes.Expressions.Types;

import compiler.Lexer.Symbol;
import compiler.Lexer.Token;
import compiler.Parser.AST.ASTNodes.Expressions.Type;

public class VoidType extends Type {

    public VoidType(Symbol s) {
        super(s);
        if(!s.getType().equals(Token.Void)) throw new IllegalArgumentException("The type of Void must be void");
    }



    public String toString(){
        return type.getValue();
    }


    public boolean equals(Object obj){
        if(!(obj instanceof VoidType)) return false;
        VoidType voidType_2=(VoidType)obj;
        return this.type.getType().equals(voidType_2.getSymbol().getType());
    }

}
