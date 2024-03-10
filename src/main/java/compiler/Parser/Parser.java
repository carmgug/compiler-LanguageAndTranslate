package compiler.Parser;
import compiler.Lexer.*;

import java.io.IOException;

public class Parser {

    private final Lexer lexer;
    private Symbol lookahead;

    public Parser(Lexer lexer) {
        this.lexer=lexer;
    }

    public void getAST() throws IOException{

        lookahead = lexer.getNextSymbol();
        while (!lookahead.isEOF()) {
            System.out.println(lookahead);
            lookahead = lexer.getNextSymbol();
        }
        System.out.println(lookahead);

    }







}
