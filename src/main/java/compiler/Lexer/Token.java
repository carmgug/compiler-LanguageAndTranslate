package compiler.Lexer;

import java.util.ArrayList;
import java.util.Arrays;

public enum Token {

    Comment(null, "Comment"),
    SpecialCharacter(new String[]{"(", ")", "{", "}", "[", "]", ".", ",", "\"", ";"}, "SpecialCharacter"),

    //Operators
    AssignmentOperator(new String[]{"="}, "AssignmentOperator"),
    ArithmeticOperator(new String[]{"+", "-", "*", "/", "%"}, "ArithmeticOperator"),
    ComparisonOperator(new String[]{"==", "<", ">", "<=", ">=", "!="}, "ComparisonOperator"),
    LogicalOperator(new String[]{"&&", "||", "!"}, "LogicalOperator"),

    IncrementOperator(new String[]{"++"}, "IncrementOperator"),

    //Tipo, forse aggiungere classi
    BasedType(new String[]{"int", "float", "bool", "string"}, "BasedType"),
    Keywords(new String[]{"final", "struct", "def", "for", "while", "if", "else"}, "Keywords"),

    //Numbers Boolean and Strign
    IntNumber(null, "Int Number"),
    FloatNumber(null, "FloatNumber"),
    BooleanValue(new String[]{"true", "false"}, "BooleanValue"),
    Identifier(null, "Identifier"),
    String(null, "String"),
    EOF(null, "EOF"),
    Whitespace(new String[]{" "}, "Whitespace"),
    NewLine(new String[]{"\n"}, "NewLine"),
    Indent(new String[]{"\t"}, "Indent"),
    UnknownToken(null, "UnknownToken");

    private final String name;

    private final ArrayList<String> strings;


    Token(String[] strings, String name) {
        if (strings != null) this.strings = new ArrayList<>(Arrays.asList(strings));
        else this.strings = null;
        this.name = name;
    }

    public boolean isEqual(String name) {
        return this.name().equals(name);
    }

    public boolean isMatch(String str) {
        return strings.contains(str);
    }


    @Override
    public String toString() {
        return name;
    }
}
