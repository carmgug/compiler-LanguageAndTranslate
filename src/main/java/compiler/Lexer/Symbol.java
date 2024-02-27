package compiler.Lexer;

import java.util.regex.Pattern;

public class Symbol {

    public Symbol(Token type,String e){
        this.type=type;
        this.e=e;
    }

    private final Token type;
    private final String e;

    public boolean isEOF(){
        return this.type.equals(Token.EOF);
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


    Comment("","Comment"),

    SpecialCharacter("<\\w+>|(|)|\\{|\\}|[|]|.|\\||,|;", "SpecialCharacter"),


    //Operators
    AssignmentOperator("=", "AssignmentOperator"),
    ArithmeticOperator("\\+|-|\\*|/|%", "ArithmeticOperator"),
    ComparisonOperator("==|<|>|<=|>=", "ComparisonOperator"),
    LogicalOperator("&&|\\|\\||!", "LogicalOperator"),

    IncrementOperator("\\+\\+", "IncrementOperator"),

    //Tipo, forse aggiungere classi
    BasedType("int|float|bool|string", "BasedType"),
    Keywords("final|struct|def|for|while|if|else|return", "Keywords"),


    //Numbers
    IntNumber("[1-9][0-9]*|0", "IntNumber"),
    FloatNumber("[-+]?[0-9][0-9]*\\.?[0-9]+", "FloatNumber"),
    BooleanValue("true|false","BooleanValue"),
    Identifier("_*[a-zA-Z][a-zA-Z0-9_]*|_[0-9][a-zA-Z0-9_]*", "Identifier"),
    EOF("", "EOF"),
    Whitespace(" ", "Whitespace"),
    NewLine("\n","NewLine"),
    Indent("\t","Indent"),
    UnknownToken("","UnknownToken");

    private final Pattern pattern;
    private final String name;


    Token(String regex, String name) {
        this.pattern = Pattern.compile("^" + regex);
        this.name = name;
    }

    public boolean match(String str){
        return pattern.matcher(str).matches();
    }

    public boolean isEqual(String name){
        return this.name().equals(name);
    }


    @Override
    public String toString() {
        return name;
    }
}


