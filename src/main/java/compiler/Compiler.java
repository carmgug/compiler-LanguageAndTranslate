/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package compiler;

import compiler.CodeGenerator.CodeGenerator;
import compiler.Exceptions.ParserExceptions.ParserException;
import compiler.Exceptions.SemanticException.SemanticException;
import compiler.Parser.AST.Program;
import compiler.SemanticAnalysis.SemanticAnalysis;

import java.io.*;


public class Compiler {
    public static void main(String[] args) throws IOException, ParserException, SemanticException, NoSuchFieldException, IllegalAccessException {

        boolean debugModeLexer = false;
        boolean debugModeParser = false;
        boolean debugModeSemanticAnalysis = false;
        String filePath = null;
        String outputFilePath = null;

        // Parse command-line arguments
        for (int i = 0; i < args.length; i++) {
            if ("-lexer".equals(args[i])) {
                debugModeLexer = true;
            } else if("-parser".equals(args[i]) || args[i] == "-parser"){
                debugModeParser = true;
            } else if("-semantic".equals(args[i]) || args[i] == "-semantic"){
                debugModeSemanticAnalysis = true;
            } else if("-o".equals(args[i])){
                // Assuming the file path comes after the -o flag
                outputFilePath = args[i+1];
                i++; // Skip the next argument
            }
            else {
                // Assuming the file path comes after the -lexer flag
                filePath = args[i];
            }
        }
        // Check if file path is provided
        if (filePath == null) {
            System.err.println("Usage: javac Main.java -lexer -parser -semantic <filepath>");
            System.exit(1); // Exit with error
        }

        FileReader fileReader = new FileReader(filePath);
        SemanticAnalysis s = new SemanticAnalysis(fileReader, debugModeLexer, debugModeParser, debugModeSemanticAnalysis);
        Program p=null;
        try{
            p=s.performSemanticAnalysis();
        }catch (SemanticException e){
            System.err.println(e.getMessage());
            System.exit(2);
        }
        System.out.println("Semantic analysis completed successfully");
        // Generate the bytecode
        CodeGenerator codeGenerator = new CodeGenerator(p,outputFilePath);
        codeGenerator.generateBytecode();






        System.out.println("Code generation completed successfully");







    }
}
