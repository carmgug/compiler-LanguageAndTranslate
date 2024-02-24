package compiler.Lexer;

import java.util.regex.Pattern;

public class Symbol {

    public Symbol(Token type,String e){
        this.type=type;
        this.e=e;
    }

    private Token type;
    private String e;

    public boolean isEOF(){
        return this.type.equals(Token.EOF);
    }

    @Override
    public String toString() {
        return "<"+type+","+e+">";
    }
}

enum Token {


    Comment("","Comment"),

    SpecialCharacter("<\\w+>|(|)|\\{|\\}|[|]|.|\\||,|;", "SpecialCharacter"),


    //Operators
    AssignmentOperator("=", "AssignmentOperator"),
    ArithmeticOperator("+|-|\\*|/|%", "ArithmeticOperator"),
    ComparisonOperator("==|<|>|<=|>=", "ComparisonOperator"),
    LogicalOperator("&&|\\|\\||!", "LogicalOperator"),

    IncrementOperator("++", "IncrementOperator"),


    //Tipo, forse aggiungere classi
    BasedType("int|float|boolean|string", "BasedType"),
    Keywords("final|stuct|def|for|while|if|else|return", "Keywords"),


    //Numbers
    IntNumber("[1-9][0-9]*", "IntNumber"),
    FloatNumber("[-+]?[0-9][0-9]*\\.?[0-9]+", "FloatNumber"), //Dopo il punto . ci va il numero o no?
    Identifier("_*[a-zA-Z][a-zA-Z0-9_]*|_[0-9][a-zA-Z0-9_]*", "Identifier"),
    EOF("", "EOF"),
    Whitespace("|\t|\n", "Whitespace"),
    UnknownToken(" ","UnknownToken");

    private final Pattern pattern;
    private final String name;


    Token(String regex, String name) {
        this.pattern = Pattern.compile("^" + regex);
        this.name = name;
    }

    public boolean match(String str){
        return pattern.matcher(str).matches();
    }



    @Override
    public String toString() {
        return name;
    }
}


