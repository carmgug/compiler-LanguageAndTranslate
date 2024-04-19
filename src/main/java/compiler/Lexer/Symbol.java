package compiler.Lexer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class Symbol {
    private final Token type;
    private final String e;
    private final int n_line;

    public Symbol(Token type,String e,int n_line){
        this.type=type;
        this.e=e;
        this.n_line=n_line;
    }

    public Symbol(Token type,String e){
        this.type=type;
        this.e=e;
        this.n_line=-1;
    }

    public static Symbol copy(Symbol value) {
        return new Symbol(value.type, value.e, value.n_line);
    }

    public boolean isEOF(){
        return this.type.equals(Token.EOF);
    }

    public int getLine(){
        return n_line;
    }

    public boolean isTypeof(String type_name){
        return type.isEqual(type_name);
    }


    public Token getType(){
        return type;
    }

    public String getValue(){
        return e;
    }



    @Override
    public String toString() {
        return "< "+type+","+e+" >";
    }
}


