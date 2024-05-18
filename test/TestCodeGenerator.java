import compiler.CodeGenerator.CodeGenerator;
import compiler.Exceptions.ParserExceptions.ParserException;
import compiler.Exceptions.SemanticException.SemanticException;
import compiler.Parser.AST.Program;
import compiler.SemanticAnalysis.SemanticAnalysis;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestCodeGenerator {

    @Test
    public void testDefaultFunction() {
        String filePath = "test/resources/TestCodeGenerator/test1/test1.lang";
        String outputFilePath= "test/resources/TestCodeGenerator/test1/test1.class";
        try {
            FileReader fileReader = new FileReader(filePath);
            SemanticAnalysis s = new SemanticAnalysis(fileReader, false, false, false);
            Program p = null;
            try {
                p = s.performSemanticAnalysis();
            } catch (SemanticException | ParserException | IOException e) {
                System.err.println(e.getMessage());
                System.exit(2);
            }
            System.out.println("Semantic analysis completed successfully");
            // Generate the bytecode
            CodeGenerator codeGenerator = new CodeGenerator(p, s.getGlobalTable(), s.getStructTable(), outputFilePath);
            codeGenerator.generateBytecode();
            //run test1.class file
            String[] command = {"java", "-cp", "test/resources/TestCodeGenerator/test1", "test1"};
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            //FileOutputStream fileOutputStream = new FileOutputStream("test/resources/TestCodeGenerator/test1/output.txt");
            Path outputFile = Paths.get("test/resources/TestCodeGenerator/test1/output.txt");
            processBuilder.redirectOutput(ProcessBuilder.Redirect.to(outputFile.toFile()));
            Process process = processBuilder.start();
            process.waitFor();
            //take the standard output of the process
            //Print input stream in output file
            int exitCode = process.exitValue();
            if (exitCode != 0) {
                throw new RuntimeException("Test failed");
            }
            // Leggi l'output del processo
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader("test/resources/TestCodeGenerator/test1/output.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }


        }catch (FileNotFoundException e){
            System.err.println("File not found");
            System.exit(1);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }

}
