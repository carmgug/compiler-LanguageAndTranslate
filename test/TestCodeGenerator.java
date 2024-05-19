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

    /*
        Test basic code generation
     */
    @Test
    public void test() {
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
            CodeGenerator codeGenerator = new CodeGenerator(p, outputFilePath);
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

    /*
        In this test we declare final array of base types and we try to iterate on it
        with a for loop and the result, using the len method on array
        Then we define 2 type of struct Person And Student
        Person have basedType as fields and Student have a Person as field and an array of grades
        We define a struct array of Person and we try to iterate on it
        and print the name of each person
        We define a struct array of Student and we try to iterate on it
        and print the name of each student and the average of the grades
     */
    @Test
    public void testIterationOnArray(){
        String filePath = "test/resources/TestCodeGenerator/test2/test2.lang";
        String outputFilePath= "test/resources/TestCodeGenerator/test2/test2.class";
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
            CodeGenerator codeGenerator = new CodeGenerator(p, outputFilePath);
            codeGenerator.generateBytecode();
            //run test1.class file
            String[] command = {"java", "-cp", "test/resources/TestCodeGenerator/test2", "test2"};
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            //FileOutputStream fileOutputStream = new FileOutputStream("test/resources/TestCodeGenerator/test1/output.txt");
            Path outputFile = Paths.get("test/resources/TestCodeGenerator/test2/output.txt");
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
            try (BufferedReader reader = new BufferedReader(new FileReader("test/resources/TestCodeGenerator/test2/output.txt"))) {
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
