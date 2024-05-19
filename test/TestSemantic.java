import compiler.Exceptions.ParserExceptions.ParserException;
import compiler.Exceptions.SemanticException.SemanticException;
import compiler.SemanticAnalysis.SemanticAnalysis;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestSemantic {

    /*
     * The files used as input for the tests are located in the test/resources folder.
     */



    //Perform semantic analysis on a file that has not errors
    @Test
    public void Test() throws IOException, ParserException {
        String filePath = "test/resources/TestSemanticFiles/test1.lang";
        FileReader fileReader = new FileReader(filePath);
        SemanticAnalysis s = new SemanticAnalysis(fileReader, false, false, false);
        try{
            s.performSemanticAnalysis();
        }catch (SemanticException e) {
            fail("Should Not Fail:" + e.getMessage());
        }
    }

    //Perform semantic analysis on a file that has errors
    //Test if the error message is correct
    @Test
    public void TestTypeConstant() throws IOException, ParserException {
        String expected_error = "TypeError: Type of the constant 's' at line 10 is not the same as the type of the right side of the assignment Expected type: string Observed type: int)";
        String filePath = "test/resources/TestSemanticFiles/test2.lang";
        FileReader fileReader = new FileReader(filePath);
        SemanticAnalysis s = new SemanticAnalysis(fileReader, false, false, false);
        try{
            s.performSemanticAnalysis();
            fail("Should Fail");
        }catch (SemanticException e) {
            if(!e.getMessage().equals(expected_error)){
                fail("Error message is not correct "+ e.getMessage()+"\nExpected: "+expected_error);
            }
        }
    }

    @Test
    public void TestTypeConstant2() throws IOException, ParserException{
        //Case 1: Should not permitted to assign a string to an int
        String input=
                "final int a=1;\n"+
                "final int b=2;\n"+
                "final int c=a+b;\n"+
                "final int d=\"carmelo\";\n";

        String expected_error="TypeError: Type of the constant 'd' at line 4 is not the same as the type of the right side of the assignment Expected type: int Observed type: string)";
        Reader r=new StringReader(input);
        SemanticAnalysis s = new SemanticAnalysis(r, true, true, false);
        try{
            s.performSemanticAnalysis();
        }catch (SemanticException e) {
            if(!e.getMessage().equals(expected_error)){
                fail("Error message is not correct "+ e.getMessage()+"\nExpected: "+expected_error);
            }
        }
        //Case 2: Should not permit to assign a float to an int
        input=
                "final int a=1;\n"+
                "final int b=2;\n"+
                "final int c=a+b;\n"+
                "final int d=1.0;\n";
        String expected_error2="TypeError: Type of the constant 'd' at line 4 is not the same as the type of the right side of the assignment Expected type: int Observed type: float)";
        r=new StringReader(input);
        s = new SemanticAnalysis(r, false, false, false);
        try{
            s.performSemanticAnalysis();
            fail("Should Fail");

        }catch (SemanticException e) {
            if(!e.getMessage().equals(expected_error2)){
                fail("Error message is not correct "+ e.getMessage()+"\nExpected: "+expected_error2);
            }
        }

        //Case 3: Should not permit to assign a boolean to an int
        input=
                "final int a=1;\n"+
                "final int b=2;\n"+
                "final int c=a+b;\n"+
                "final int d=true;\n";
        String expected_error3="TypeError: Type of the constant 'd' at line 4 is not the same as the type of the right side of the assignment Expected type: int Observed type: bool)";
        r=new StringReader(input);
        s = new SemanticAnalysis(r, false, false, false);
        try{
            s.performSemanticAnalysis();
            fail("Should Fail");

        }catch (SemanticException e) {
            if(!e.getMessage().equals(expected_error3)){
                fail("Error message is not correct "+ e.getMessage()+"\nExpected: "+expected_error3);
            }
        }
        //Case 4: Should not permit to assign a boolean to an float
        input=
                "final int a=1;\n"+
                "final int b=2;\n"+
                "final int c=a+b;\n"+
                "final float d=true;\n";
        String expected_error4="TypeError: Type of the constant 'd' at line 4 is not the same as the type of the right side of the assignment Expected type: float Observed type: bool)";
        r=new StringReader(input);
        s = new SemanticAnalysis(r, false, false, false);
        try{
            s.performSemanticAnalysis();
            fail("Should Fail");

        }catch (SemanticException e) {
            if(!e.getMessage().equals(expected_error4)){
                fail("Error message is not correct "+ e.getMessage()+"\nExpected: "+expected_error4);
            }
        }
        //Case 5: Should not permit to assign a boolean to an string
        input=
                "final int a=1;\n"+
                "final int b=2;\n"+
                "final int c=a+b;\n"+
                "final string d=true;\n";
        String expected_error5="TypeError: Type of the constant 'd' at line 4 is not the same as the type of the right side of the assignment Expected type: string Observed type: bool)";
        r=new StringReader(input);
        s = new SemanticAnalysis(r, false, false, false);
        try{
            s.performSemanticAnalysis();
            fail("Should Fail");

        }catch (SemanticException e) {
            if(!e.getMessage().equals(expected_error5)){
                fail("Error message is not correct "+ e.getMessage()+"\nExpected: "+expected_error5);
            }
        }

        //Case 6: should not permit to assign a Array of int to an int
        input=
                "final int a=1;\n"+
                "final int b=2;\n"+
                "final int c=a+b;\n"+
                "final int d={1,2,3};\n";
        String expected_error6="TypeError: Type of the constant 'd' at line 4 is not the same as the type of the right side of the assignment Expected type: int Observed type: Array of int)";
        r=new StringReader(input);
        s = new SemanticAnalysis(r, false, false, false);
        try {
            s.performSemanticAnalysis();
            fail("Should Fail");

        }catch (SemanticException e) {
            if(!e.getMessage().equals(expected_error6)){
                fail("Error message is not correct "+ e.getMessage()+"\nExpected: "+expected_error6);
            }
        }
    }

    @Test
    public void TestStructErrors() throws ParserException, IOException {
        //Case 1: Should not permit to define a struct with the same name of a default type
        //We catch already in the parser beacuse after a keystruct we can only have an identifier
        //And an identifier can't be a default type
        String input=
                "struct int{\n"+
                "int a;\n"+
                "}\n";
        String expected_error="Unexpected token: int expected: Identifier at line 1";
        Reader r=new StringReader(input);
        SemanticAnalysis s = new SemanticAnalysis(r, false, false, false);
        try{
            s.performSemanticAnalysis();
            fail("Should Fail");

        }catch (Exception e) {
            if(!e.getMessage().equals(expected_error)){
                fail("Error message is not correct "+ e.getMessage()+"\nExpected: "+expected_error);
            }
        }
        //Case 2 Should not permit to define a struct with while as a name
        input=
                "struct while{\n"+
                        "int a;\n"+
                        "}\n";
        expected_error="Unexpected token: while expected: Identifier at line 1";
        r=new StringReader(input);
        s = new SemanticAnalysis(r, false, false, false);
        try{
            s.performSemanticAnalysis();
            fail("Should Fail");

        }catch (Exception e) {
            if(!e.getMessage().equals(expected_error)){
                fail("Error message is not correct "+ e.getMessage()+"\nExpected: "+expected_error);
            }
        }

        //Case 2: Should not permit to define a struct with the same name of another struct
        input=
                "struct Person{\n"+
                "int a;\n"+
                "}\n"+
                "struct Person{\n"+
                "int a;\n"+
                "}\n";
        expected_error="StructError: Struct Person already defined (line 4)";
        r=new StringReader(input);
        s = new SemanticAnalysis(r, false, false, false);
        try{
            s.performSemanticAnalysis();
            fail("Should Fail");

        }catch (SemanticException e) {
            if(!e.getMessage().equals(expected_error)){
                fail("Error message is not correct "+ e.getMessage()+"\nExpected: "+expected_error);
            }
        }
    }

    @Test
    public void TestOperator() throws ParserException, IOException {
        //Case 1: Should not permit to use string with the operator +
        String input=
                "final string a=\"ciao\";\n"+
                "final string b=\"ciao\";\n"+
                "final string c=a+b;\n";
        String expected_error="OperatorError: Operator: {Type: AdditiveOperator,Value: +} doesn't allow type (string)";
        Reader r=new StringReader(input);
        SemanticAnalysis s = new SemanticAnalysis(r, false, false, false);
        try{
            s.performSemanticAnalysis();
            fail("Should Fail");

        }catch (SemanticException e) {
            if(!e.getMessage().equals(expected_error)){
                fail("Error message is not correct "+ e.getMessage()+"\nExpected: "+expected_error);
            }
        }
        //Case 2: Should not permit to use string with the operator -
        input=
                "final string a=\"ciao\";\n"+
                "final string b=\"ciao\";\n"+
                "final string c=a-b;\n";
        expected_error="OperatorError: Operator: {Type: AdditiveOperator,Value: -} doesn't allow type (string)";
        //Case 2: Should not permit to use string with the operator *
        input=
                "final string a=\"ciao\";\n"+
                "final string b=\"ciao\";\n"+
                "final string c=a*b;\n";
        expected_error="OperatorError: Operator: {Type: MultiplicativeOperator,Value: *} doesn't allow type (string)";
        r=new StringReader(input);
        s = new SemanticAnalysis(r, false, false, false);
        try{
            s.performSemanticAnalysis();
            fail("Should Fail");

        }catch (SemanticException e) {
            if(!e.getMessage().equals(expected_error)){
                fail("Error message is not correct "+ e.getMessage()+"\nExpected: "+expected_error);
            }
        }
        //Case 3: Should not permit to use string with the operator /
        input=
                "final string a=\"ciao\";\n"+
                "final string b=\"ciao\";\n"+
                "final string c=a/b;\n";
        expected_error="OperatorError: Operator: {Type: MultiplicativeOperator,Value: /} doesn't allow type (string)";
        r=new StringReader(input);
        s = new SemanticAnalysis(r, false, false, false);
        try{
            s.performSemanticAnalysis();
        }catch (SemanticException e) {
            if(!e.getMessage().equals(expected_error)){
                fail("Error message is not correct "+ e.getMessage()+"\nExpected: "+expected_error);
            }
        }
        //Case 4: Should not permit to use string with the operator %
        input=
                "final string a=\"ciao\";\n"+
                "final string b=\"ciao\";\n"+
                "final string c=a%b;\n";
        expected_error="OperatorError: Operator: {Type: MultiplicativeOperator,Value: %} doesn't allow type (string)";
        r=new StringReader(input);
        s = new SemanticAnalysis(r, false, false, false);
        try{
            s.performSemanticAnalysis();
            fail("Should Fail");

        }catch (SemanticException e) {
            if(!e.getMessage().equals(expected_error)){
                fail("Error message is not correct "+ e.getMessage()+"\nExpected: "+expected_error);
            }
        }
        //Case 5: Sould not permit to use bool with the operator +
        input=
                "final bool a=true;\n"+
                "final bool b=true;\n"+
                "final bool c=a+b;\n";
        expected_error="OperatorError: Operator: {Type: AdditiveOperator,Value: +} doesn't allow type (bool)";
        r=new StringReader(input);
        s = new SemanticAnalysis(r, false, false, false);
        try{
            s.performSemanticAnalysis();
        }catch (SemanticException e) {
            if(!e.getMessage().equals(expected_error)){
                fail("Error message is not correct "+ e.getMessage()+"\nExpected: "+expected_error);
            }
        }
        //Case 6: Sould not permit to use bool with the operator -
        input=
                "final bool a=true;\n"+
                "final bool b=true;\n"+
                "final bool c=a-b;\n";
        expected_error="OperatorError: Operator: {Type: AdditiveOperator,Value: -} doesn't allow type (bool)";
        r=new StringReader(input);
        s = new SemanticAnalysis(r, false, false, false);
        try{
            s.performSemanticAnalysis();
            fail("Should Fail");

        }catch (SemanticException e) {
            if(!e.getMessage().equals(expected_error)){
                fail("Error message is not correct "+ e.getMessage()+"\nExpected: "+expected_error);
            }
        }
        //Case 7: Sould not permit to use bool with the operator *
        input=
                "final bool a=true;\n"+
                "final bool b=true;\n"+
                "final bool c=a*b;\n";
        expected_error="OperatorError: Operator: {Type: MultiplicativeOperator,Value: *} doesn't allow type (bool)";
        r=new StringReader(input);
        s = new SemanticAnalysis(r, false, false, false);
        try{
            s.performSemanticAnalysis();
            fail("Should Fail");

        }catch (SemanticException e) {
            if(!e.getMessage().equals(expected_error)){
                fail("Error message is not correct "+ e.getMessage()+"\nExpected: "+expected_error);
            }
        }
        //Case 8: Sould not permit to use Struct with the operator +
        input=
                "struct Person{\n"+
                "int a;\n"+
                "}\n"+
                "Person a=Person(1);\n"+
                "Person b=Person(1);\n"+
                "Person c=a+b;\n";
        expected_error="OperatorError: Operator: {Type: AdditiveOperator,Value: +} doesn't allow type (Person)";
        r=new StringReader(input);
        s = new SemanticAnalysis(r, false, false, false);
        try{
            s.performSemanticAnalysis();
            fail("Should Fail");

        }catch (SemanticException e) {
            if(!e.getMessage().equals(expected_error)){
                fail("Error message is not correct "+ e.getMessage()+"\nExpected: "+expected_error);
            }
        }

    }

    @Test
    public void TestProcedureAndConstructor() throws ParserException, IOException {
        TestProcedureParameters();
        TestStructConstructor();


    }

    private void TestStructConstructor() throws ParserException, IOException{
        //Case 1: Should not permit to call a constructor with the wrong number of parameters
        String input=
                "struct Person{\n"+
                "int a;\n"+
                "}\n"+
                "Person a=Person(4.0,2);\n";
        String expected_error="ArgumentError: Constructor call 'Person' has not the same parameters as the struct definition(line 4)";
        Reader r=new StringReader(input);
        SemanticAnalysis s = new SemanticAnalysis(r, false, false, false);
        try{
            s.performSemanticAnalysis();
            fail("Should Fail");
        }catch (SemanticException e) {
            if(!e.getMessage().equals(expected_error)){
                fail("Error message is not correct "+ e.getMessage()+"\nExpected: "+expected_error);
            }
        }
        //Case 2: Should not permit to call a constructor with the wrong type of parameters
        input=
                "struct Person{\n"+
                "int a;\n"+
                "}\n"+
                "Person a=Person(true);\n";
        expected_error="ArgumentError: Constructor call 'Person' has not the same parameters as the struct definition(line 4) Expected: int Observed: bool";
        r=new StringReader(input);
        s = new SemanticAnalysis(r, false, false, false);
        try{
            s.performSemanticAnalysis();
            fail("Should Fail");
        }catch (SemanticException e) {
            if(!e.getMessage().equals(expected_error)){
                fail("Error message is not correct "+ e.getMessage()+"\nExpected: "+expected_error);
            }
        }
    }

    private void TestProcedureParameters() throws ParserException, IOException {
        //Case 1: Should not permit to pass a float to a function that expects an int
        String input= "def float p(int a, float b){\n"+
                        "float c=a+b;\n"+
                        "return c;\n"+
                "}\n"+
                "int r=p(4.0,2);\n";
        String expected_error="ArgumentError: Function call 'p' has not the same parameters as the function definition(line 5)";
        Reader r=new StringReader(input);
        SemanticAnalysis s = new SemanticAnalysis(r, false, false, false);
        try{
            s.performSemanticAnalysis();
            fail("Should Fail");
        }catch (SemanticException e) {
            if(!e.getMessage().equals(expected_error)){
                fail("Error message is not correct "+ e.getMessage()+"\nExpected: "+expected_error);
            }
        }
        //Case 2: Should not permit to declare a function with the same name and same parameters
        input= "def int p(int a, float b){\n"+
                        "float c=a+b;\n"+
                        "return 0;\n"+
                "}\n"+
                "def int p(int a, float b){\n"+
                        "float c=a+b;\n"+
                        "return 0;\n"+
                "}\n";
        expected_error="ScopeError: You have already define a procedure with the same name and same parameters(line: 5)";
        r=new StringReader(input);
        s = new SemanticAnalysis(r, false, false, false);
        try{
            s.performSemanticAnalysis();
            fail("Should Fail");
        }catch (SemanticException e) {
            if(!e.getMessage().equals(expected_error)){
                fail("Error message is not correct "+ e.getMessage()+"\nExpected: "+expected_error);
            }
        }
    }

    @Test
    public void TestLoopConditions() throws ParserException, IOException {
        //Case 1: Should not permit to pass a float to a function that expects an int
        String input=
                "struct Persona{\n" +
                "int eta;\n" +
                "}\n" +
                "def boolean p(){\n" +
                "Persona marco=Persona(3)\n;" +
                "int i=0;\n" +
                "while(marco.eta){\n" +
                "}\n" +
                "}";
        String expected_error="MissingConditionError: The condition of the while statement is not a boolean (line 7)";
        Reader r=new StringReader(input);
        SemanticAnalysis s = new SemanticAnalysis(r, false, false, false);
        try{
            s.performSemanticAnalysis();
            fail("Should Fail");
        }catch (SemanticException e) {
            if(!e.getMessage().equals(expected_error)){
                fail("Error message is not correct "+ e.getMessage()+"\nExpected: "+expected_error);
            }
        }

        input= "def boolean p(){\n" +
                    "int i=0;\n" +
                    "for(i=0,i,i++){\n" +
                    "}\n" +
                "}";
        String expected_error2="MissingConditionError: The condition of the for statement is not a boolean (line 3)";
        r=new StringReader(input);
        s = new SemanticAnalysis(r, false, false, false);
        try{
            s.performSemanticAnalysis();
            fail("Should Fail");
        }catch (SemanticException e) {
            if(!e.getMessage().equals(expected_error2)){
                fail("Error message is not correct "+ e.getMessage()+"\nExpected: "+expected_error2);
            }
        }
        input= "def boolean p(){\n" +
                "float[] flag={0,1.1};\n" +
                "if(flag[1]){\n"+
                "}\n" +
                "}";
        String expected_error3="MissingConditionError: The condition of the if statement is not a bool (at line: +3)";
        r=new StringReader(input);
        s= new SemanticAnalysis(r, false, false, false);
        try{
            s.performSemanticAnalysis();
            fail("Should Fail");
        }catch (SemanticException e) {
            if(!e.getMessage().equals(expected_error3)){
                fail("Error message is not correct "+ e.getMessage()+"\nExpected: "+expected_error3);
            }
        }
    }

    @Test
    public void TestReturn() throws ParserException, IOException {
        //Case 1: Should not permit to pass a float to a function that expects an int
        String input=
                "struct Persona{\n" +
                        "int eta;\n" +
                        "}\n" +
                        "def Persona p(){\n" +
                        "int i=3;\n" +
                        "int j=5;\n" +
                        "int somma=i+j;\n" +
                        "return somma;\n" +
                        "}";
        String expected_error="ReturnError: Type of the return statement  is not the same of the type of the curr Procedure Expected type: Persona Observed type: int at line: 8)";
        Reader r=new StringReader(input);
        SemanticAnalysis s = new SemanticAnalysis(r, false, false, false);
        try{
            s.performSemanticAnalysis();
            fail("Should Fail");
        }catch (SemanticException e) {
            if(!e.getMessage().equals(expected_error)){
                fail("Error message is not correct "+ e.getMessage()+"\nExpected: "+expected_error);
            }
        }

        input="struct Persona{\n" +
                        "int eta;\n" +
                        "}\n" +
                        "def Persona p(){\n" +
                        "Persona giulio=Persona(18);\n" +
                        "}";
        expected_error="ReturnError: Procedure p does not contain at least one return statement (line 4)";
        r=new StringReader(input);
        s = new SemanticAnalysis(r, false, false, false);
        try{
            s.performSemanticAnalysis();
            fail("Should Fail");
        }catch (SemanticException e) {
            if(!e.getMessage().equals(expected_error)){
                fail("Error message is not correct "+ e.getMessage()+"\nExpected: "+expected_error);
            }
        }


    }


    @Test
    public void TestScope() throws ParserException, IOException {
        String input=
                "def int p(){\n"+
                    "int a=1;\n"+
                    "int b=4+z;\n"+
                    "int z=5+a;\n"+
                    "int b=2;\n"+
                    "return c;\n"+
                "}\n"+
                "int a=1;\n"+
                "int b=2;\n"+
                "int c=a+b;\n"+
                "int d=p();\n";
        String expected_error="ScopeError: Variable 'z' not defined at line 3";
        Reader r=new StringReader(input);
        SemanticAnalysis s = new SemanticAnalysis(r, false, false, false);
        try{
            s.performSemanticAnalysis();
            fail("Should Fail");
        }catch (SemanticException e) {
            if(!e.getMessage().equals(expected_error)){
                fail("Error message is not correct "+ e.getMessage()+"\nExpected: "+expected_error);
            }
        }
    }









}
