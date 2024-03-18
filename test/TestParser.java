import compiler.Lexer.Symbol;
import compiler.Lexer.Token;
import compiler.Parser.AST.ASTNodes.Struct;
import compiler.Parser.AST.ASTNodes.VariableDeclaration;
import compiler.Parser.AST.Program;
import compiler.Parser.Parser;
import org.junit.Test;
import org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import compiler.Lexer.Lexer;

import static org.junit.Assert.*;

public class TestParser {

    @Test
    public void testStructDefinition() {
        String sourceCode = "struct Point {\n" +
                "    int x;\n" +
                "    int y;\n" +
                "}";
        Lexer lexer = new Lexer(new StringReader(sourceCode), false);
        Parser parser = new Parser(lexer);

        try {
            Program program = parser.getAST();
            assertEquals(1, program.getStructs().size());
        } catch (IOException e) {
            fail("Exception thrown during test: " + e.toString());
        }
    }

    @Test
    public void testProcedureDefinition() {
        String sourceCode = "def int square(int v) {\n" +
                "    return v*v;\n" +
                "}";
        Lexer lexer = new Lexer(new StringReader(sourceCode), false);
        Parser parser = new Parser(lexer);

        try {
            Program program = parser.getAST();
            assertEquals(1, program.getProcedures().size());
        } catch (IOException e) {
            fail("Exception thrown during test: " + e.toString());
        }
    }

    @Test
    public void testVariableDeclaration() {
        String sourceCode = "int a = 3;";
        Parser parser = new Parser(new StringReader(sourceCode), true,true);
        try {
            Program program = parser.getAST();

            assertEquals(1, program.getGlobal_variables().size());
        } catch (IOException e) {
            fail("Exception thrown during test: " + e.toString());
        }
    }
}
