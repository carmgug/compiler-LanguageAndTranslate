package compiler.Lexer;

import java.util.ArrayList;
import java.util.Arrays;

public enum Token {

    Comment(null, "Comment"),
    SpecialCharacter(new String[]{"(", ")", "{", "}", "[", "]", ".", ",", "\"", ";"}, "SpecialCharacter"),
    OpeningParenthesis(new String[]{"("}, "OpeningParenthesis"),
    ClosingParenthesis(new String[]{")"}, "ClosingParenthesis"),
    OpeningCurlyBrace(new String[]{"{"}, "OpeningCurlyBrace"),
    ClosingCurlyBrace(new String[]{"}"}, "ClosingCurlyBrace"),
    OpeningSquareBracket(new String[]{"["}, "OpeningSquareBracket"),
    ClosingSquareBracket(new String[]{"]"}, "ClosingSquareBracket"),
    Dot(new String[]{"."}, "Dot"),
    Comma(new String[]{","}, "Comma"),
    DoubleQuote(new String[]{"\""}, "DoubleQuote"),

    Semicolon(new String[]{";"}, "Semicolon"),



    //Operators
    AssignmentOperator(new String[]{"="}, "AssignmentOperator"),
    ArithmeticOperator(new String[]{"+", "-", "*", "/", "%"}, "ArithmeticOperator"),

    AdditiveOperator(new String[]{"+", "-"}, "AdditiveOperator"),

    MultiplicativeOperator(new String[]{"*", "/", "%"}, "MultiplicativeOperator"),



    ComparisonOperator(new String[]{"==", "<", ">", "<=", ">=", "!="}, "ComparisonOperator"),
    LogicalOperator(new String[]{"&&", "||", "!"}, "LogicalOperator"),

    IncrementOperator(new String[]{"++"}, "IncrementOperator"),

    //Tipo, forse aggiungere classi
    BasedType(new String[]{"int", "float", "bool", "string"}, "BasedType"),


    //Void added for function return type after the first assigment
    Void(new String[]{"void"}, "void"),

    Keywords(new String[]{"final", "struct", "def", "for", "while", "if", "else","return"}, "Keywords"),
    Final(new String[]{"final"}, "final"),
    Struct(new String[]{"struct"}, "struct"),
    Def(new String[]{"def"}, "def"),
    For(new String[]{"for"}, "for"),
    While(new String[]{"while"}, "while"),
    If(new String[]{"if"}, "if"),

    Return(new String[]{"return"},"return"),
    Else(new String[]{"else"}, "else"),
    //Numbers Boolean and Strign
    IntNumber(null, "int"),
    FloatNumber(null, "float"),
    BooleanValue(new String[]{"true", "false"}, "bool"),
    Identifier(null, "Identifier"),
    String(null, "string"),
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
