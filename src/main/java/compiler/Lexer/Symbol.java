package compiler.Lexer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class Symbol {

    public Symbol(Token type,String e,int n_line){
        this.type=type;
        this.e=e;
        this.n_line=n_line;
    }

    private final Token type;
    private final String e;

    private final int n_line;

    public boolean isEOF(){
        return this.type.equals(Token.EOF);
    }

    public int getLine(){
        return n_line;
    }

    public boolean isTypeof(String type_name){
        return type.isEqual(type_name);
    }

    @Override
    public String toString() {
        return "< "+type+","+e+" >";
    }
}

enum Token {


    Comment( null, "Comment"),
    SpecialCharacter(new String[]{"(", ")", "{", "}","[","]",".",",","\"",";"}, "SpecialCharacter"),

    //Operators
    AssignmentOperator( new String[]{"="}, "AssignmentOperator"),
    ArithmeticOperator(new String[]{"+", "-", "*", "/","%"}, "ArithmeticOperator"),
    ComparisonOperator( new String[]{"==", "<", ">", "<=",">=","!="}, "ComparisonOperator"),
    LogicalOperator( new String[]{"&&", "||", "!"}, "LogicalOperator"),

    IncrementOperator( new String[]{"++"}, "IncrementOperator"),

    //Tipo, forse aggiungere classi
    BasedType(new String[]{"int","float","bool","string"}, "BasedType"),
    Keywords( new String[]{"final","struct","def","for","while","if","else"}, "Keywords"),

    //Numbers Boolean and Strign
    IntNumber( null, "Int Number"),
    FloatNumber( null, "FloatNumber"),
    BooleanValue( new String[]{"true", "false"}, "BooleanValue"),
    Identifier( null, "Identifier"),
    String(null,"String"),
    EOF( null, "EOF"),
    Whitespace( new String[]{" "}, "Whitespace"),
    NewLine( new String[]{"\n"}, "NewLine"),
    Indent(new String[]{"\t"}, "Indent"),
    UnknownToken(null, "UnknownToken");

    private final String name;

    private final ArrayList<String> strings;


    Token(String[] strings, String name) {
        if(strings!=null) this.strings=new ArrayList<>(Arrays.asList(strings));
        else this.strings=null;
        this.name = name;
    }

    public boolean isEqual(String name){
        return this.name().equals(name);
    }

    public boolean isMatch(String str){
        return strings.contains(str);
    }


    @Override
    public String toString() {
        return name;
    }
}


