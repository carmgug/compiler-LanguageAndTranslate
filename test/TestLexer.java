import static org.junit.Assert.assertNotNull;

import compiler.Lexer.Symbol;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import compiler.Lexer.Lexer;

public class TestLexer {
    
    @Test
    public void test() throws IOException {
        String input = "var x int = 2;";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        assertNotNull(lexer.getNextSymbol());

    }

}
