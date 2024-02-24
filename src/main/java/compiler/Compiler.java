/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package compiler;

import compiler.Lexer.Lexer;
import compiler.Lexer.Symbol;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.assertNotNull;

public class Compiler {
    public static void main(String[] args) throws IOException {

        System.out.println("Hello from the compiler !");
        String input = "float x int = 2.4 && 5 //Prova codice \\n int y=3 ";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        while(true) {
            Symbol s=lexer.getNextSymbol();
            System.out.println(s);
            if(s.isEOF())
                break;
        }

    }
}
