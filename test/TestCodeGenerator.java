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

import static org.junit.Assert.assertEquals;

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
            CodeGenerator codeGenerator = new CodeGenerator(p, s.getGlobalTable(),outputFilePath);
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
            CodeGenerator codeGenerator = new CodeGenerator(p, s.getGlobalTable(),outputFilePath);
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
            String expected_Output=
                    "int array: 1 2 3 4 5 6 7 8 9 10 \n" +
                    "float array: 1.0 2.0 3.0 4.0 5.0 6.0 7.0 8.0 9.0 10.0 \n" +
                    "bool array: true false true false true false true false true false \n" +
                    "string array: a b c d e f g h i j \n" +
                    "Person array: John Jane Jack Jill James Jenny \n" +
                    "Student array: \n" +
                    "John 90.0 80.0 79.0 60.0 50.0 \n" +
                    "Average grades: 71.8\n" +
                    "\n" +
                    "Jane 85.0 75.0 63.0 55.0 45.0 \n" +
                    "Average grades: 64.6\n" +
                    "\n" +
                    "Jack 95.0 85.0 72.0 65.0 55.0 \n" +
                    "Average grades: 74.4\n" +
                    "\n" +
                    "Jill 80.0 70.0 64.0 50.0 40.0 \n" +
                    "Average grades: 60.8\n" +
                    "\n" +
                    "James 100.0 94.0 82.0 70.0 60.0 \n" +
                    "Average grades: 81.2\n" +
                    "\n" +
                    "Jenny 72.0 61.0 53.0 42.0 35.2 \n" +
                    "Average grades: 52.640003\n\n";
            assertEquals(expected_Output,output.toString());

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
        In this test we will declare a searies of nested struct and we will try to access the fields of the nested struct
        and perform operation on them
        Person : name, age,books (Book[]), address (Address)
        Address : street (string), city (string), cordinate (Point)
        Point : x (int), y (int)
        Book : title (string), authors (string[]), year (int)
        We will have 3 nested loops:
            1. Iterate through the array of persons and print the name and age of each person
            2. Iterate through the list of books of each person and print the title
            3. Iterate through the authors of each book and print the author
        We will use the len method to get the length of the array of books
        And use different loops (while and for)
     */
    @Test
    public void nestedStruct(){

        String filePath = "test/resources/TestCodeGenerator/test3/test3.lang";
        String outputFilePath= "test/resources/TestCodeGenerator/test3/test3.class";
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
            CodeGenerator codeGenerator = new CodeGenerator(p, s.getGlobalTable(),outputFilePath);
            codeGenerator.generateBytecode();
            //run test1.class file
            String[] command = {"java", "-cp", "test/resources/TestCodeGenerator/test3", "test3"};
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            //FileOutputStream fileOutputStream = new FileOutputStream("test/resources/TestCodeGenerator/test1/output.txt");
            Path outputFile = Paths.get("test/resources/TestCodeGenerator/test3/output.txt");
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
            try (BufferedReader reader = new BufferedReader(new FileReader("test/resources/TestCodeGenerator/test3/output.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            String expected_Output=
                    "Performing a visit to Jhon\n" +
                            "She/He lives in Main St. in Springfield at coordinates 10 20\n" +
                            "She/He has 3 books:\n" +
                            "Book 0 is The Art of Computer Programming by D. Knuth published in 1968\n" +
                            "Book 1 is Refactoring by M. Fowler, K. Beck published in 1999\n" +
                            "Book 2 is Design Patterns by E. Gamma, R. Helm, R. Johnson, J. Vlissides published in 1994\n" +
                            "Performing a visit to Victor\n" +
                            "She/He lives in Main St. in Springfield at coordinates 10 20\n" +
                            "She/He has 3 books:\n" +
                            "Book 0 is The Art of Computer Programming by D. Knuth published in 1968\n" +
                            "Book 1 is Refactoring by M. Fowler, K. Beck published in 1999\n" +
                            "Book 2 is Design Patterns by E. Gamma, R. Helm, R. Johnson, J. Vlissides published in 1994\n" +
                            "Performing a visit to Alice\n" +
                            "She/He lives in Main St. in Springfield at coordinates 10 20\n" +
                            "She/He has 3 books:\n" +
                            "Book 0 is The Art of Computer Programming by D. Knuth published in 1968\n" +
                            "Book 1 is Refactoring by M. Fowler, K. Beck published in 1999\n" +
                            "Book 2 is Design Patterns by E. Gamma, R. Helm, R. Johnson, J. Vlissides published in 1994\n";
            assertEquals(expected_Output,output.toString());

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
        In this test we will declare an array
        and use as index a complex expression
        the sum between 4 variables
            1. persons[0].favoritePlaces[peoples[getOne()].favoritePlaces[getIndex(x)-1].x].x
            2. getOne()
            3. where x inside x is a constant variable
            4. and x after . is a field of the struct
        Note that favoritePlaces is an array of Point
        so:
            int[] array= {1,2,3,4,5};
            int result= array[person.favoritePlaces[getIndex()].x+getOne()+GlobalVaribale+Constant];
            Repeat this with different type of array
            float,bool, string, and Struct
        Also added infinite loop at the end with return;

     */
    @Test
    public void arrayAccess(){
        String filePath = "test/resources/TestCodeGenerator/test4/test4.lang";
        String outputFilePath= "test/resources/TestCodeGenerator/test4/test4.class";
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
            CodeGenerator codeGenerator = new CodeGenerator(p,s.getGlobalTable() ,outputFilePath);
            codeGenerator.generateBytecode();
            //run test1.class file
            String[] command = {"java", "-cp", "test/resources/TestCodeGenerator/test4", "test4"};
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            //FileOutputStream fileOutputStream = new FileOutputStream("test/resources/TestCodeGenerator/test1/output.txt");
            Path outputFile = Paths.get("test/resources/TestCodeGenerator/test4/output.txt");
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
            try (BufferedReader reader = new BufferedReader(new FileReader("test/resources/TestCodeGenerator/test4/output.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            String expected_Output=
                   "Accessing on an element of an array of int with a complex expression: 8\n" +
                   "Accessing on an element of an array of float with a complex expression: 8.0\n" +
                   "Accessing on an element of an array of bool with a complex expression: false\n" +
                   "Accessing on an element of an array of string with a complex expression: 8th element\n" +
                   "Accessing on an element of an array of struct with a complex expression: Point(15,16)\n";
            assertEquals(expected_Output,output.toString());

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
