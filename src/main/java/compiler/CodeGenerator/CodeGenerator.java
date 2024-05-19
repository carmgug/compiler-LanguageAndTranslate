package compiler.CodeGenerator;

import compiler.Parser.AST.ASTNode;
import compiler.Parser.AST.Program;
import compiler.SemanticAnalysis.SymbolTable.SymbolTable;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;


public class CodeGenerator extends ClassLoader {


    private static ClassWriter cw;
    private Program program;
    private String programName;
    private ScopesTable table;
    private CodeGenerationVisitor codeGenerationVisitor;
    private String outputFilePath;
    private String path;



    public CodeGenerator(Program p, String outputFilePath) {
        this.program = p;
        this.table = new ScopesTable();
        this.outputFilePath = outputFilePath;
        //As the program name use the string between / and .class of the file path
        this.programName = outputFilePath.substring(outputFilePath.lastIndexOf('/') + 1, outputFilePath.lastIndexOf('.'));
        //As the path use the string between first character and last / of the file path
        this.path = outputFilePath.substring(0, outputFilePath.lastIndexOf('/') + 1);
    }


    private void defineLen(){
        //len method take a string or an array and return the length of the string or the array
        //input string return an integer
        //is public and static
        MethodVisitor mv = cw.visitMethod
                (Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "len", "(Ljava/lang/String;)I", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "length", "()I", false);
        mv.visitInsn(Opcodes.IRETURN);
        mv.visitMaxs(-1, -1);
        mv.visitEnd();
        //define len method for array
        //input array return an integer
        //is public and static
        mv = cw.visitMethod
                (Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "len", "([I)I", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitInsn(Opcodes.ARRAYLENGTH);
        mv.visitInsn(Opcodes.IRETURN);
        mv.visitMaxs(-1, -1);
        mv.visitEnd();
        //define len method for array of Float
        //input array return an integer
        //is public and static
        mv = cw.visitMethod
                (Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "len", "([F)I", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitInsn(Opcodes.ARRAYLENGTH);
        mv.visitInsn(Opcodes.IRETURN);
        mv.visitMaxs(-1, -1);
        mv.visitEnd();
        //define len method for array of boolean
        //input array return an integer
        //is public and static
        mv = cw.visitMethod
                (Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "len", "([Z)I", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitInsn(Opcodes.ARRAYLENGTH);
        mv.visitInsn(Opcodes.IRETURN);
        mv.visitMaxs(-1, -1);
        mv.visitEnd();
        //define len method for array of string
        //input array return an integer
        //is public and static
        mv = cw.visitMethod
                (Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "len", "([Ljava/lang/String;)I", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitInsn(Opcodes.ARRAYLENGTH);
        mv.visitInsn(Opcodes.IRETURN);
        mv.visitMaxs(-1, -1);
        mv.visitEnd();
    }



    private void defineChr(){
        // Method to convert an integer to a character
        //input integer return a string
        //is public and static
        MethodVisitor mv = cw.visitMethod
                (Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "chr", "(I)Ljava/lang/String;", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ILOAD, 0);
        mv.visitInsn(Opcodes.I2C);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/String", "valueOf", "(C)Ljava/lang/String;", false);
        mv.visitInsn(Opcodes.ARETURN);
        mv.visitMaxs(-1, -1);
        mv.visitEnd();
    }

    private void defineReadInt(){
        // Method to read an integer from the console
        //input void return an integer
        //is public and static
        MethodVisitor mv = cw.visitMethod
                (Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "readInt", "()I", null, null);
        mv.visitCode();
        mv.visitTypeInsn(Opcodes.NEW, "java/util/Scanner");
        mv.visitInsn(Opcodes.DUP);
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "in", "Ljava/io/InputStream;");
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/Scanner", "<init>", "(Ljava/io/InputStream;)V", false);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/Scanner", "nextInt", "()I", false);
        mv.visitInsn(Opcodes.IRETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    private void defineReadFloat(){

        // Method to read a float from the console
        //input void return a float
        //is public and static
        MethodVisitor mv = cw.visitMethod
                (Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "readFloat", "()F", null, null);
        mv.visitCode();
        mv.visitTypeInsn(Opcodes.NEW, "java/util/Scanner");
        mv.visitInsn(Opcodes.DUP);
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "in", "Ljava/io/InputStream;");
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/Scanner", "<init>", "(Ljava/io/InputStream;)V", false);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/Scanner", "nextFloat", "()F", false);
        mv.visitInsn(Opcodes.FRETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    private void defineReadString(){

            // Method to read a string from the console
            //input void return a string
            //is public and static
            MethodVisitor mv = cw.visitMethod
                    (Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "readString", "()Ljava/lang/String;", null, null);
            mv.visitCode();
            mv.visitTypeInsn(Opcodes.NEW, "java/util/Scanner");
            mv.visitInsn(Opcodes.DUP);
            mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "in", "Ljava/io/InputStream;");
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/Scanner", "<init>", "(Ljava/io/InputStream;)V", false);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/Scanner", "nextLine", "()Ljava/lang/String;", false);
            mv.visitInsn(Opcodes.ARETURN);
            mv.visitMaxs(-1, -1);
            mv.visitEnd();

    }



    private void defineWrite(){
        // Method to write a string to the console
        //input string return void
        //is public and static
        //print string to the console
        MethodVisitor mv = cw.visitMethod
                (Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "write", "(Ljava/lang/String;)V", null, null);
        mv.visitCode();
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "print", "(Ljava/lang/String;)V", false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(-1, -1);
        mv.visitEnd();
        //print int to the console
        mv = cw.visitMethod
                (Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "write", "(I)V", null, null);
        mv.visitCode();
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitVarInsn(Opcodes.ILOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "print", "(I)V", false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(-1, -1);
        mv.visitEnd();

        //print float to the console
        mv = cw.visitMethod
                (Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "write", "(F)V", null, null);
        mv.visitCode();
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitVarInsn(Opcodes.FLOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "print", "(F)V", false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(-1, -1);
        mv.visitEnd();

        //print boolean to the console
        mv = cw.visitMethod
                (Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "write", "(Z)V", null, null);
        mv.visitCode();
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitVarInsn(Opcodes.ILOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "print", "(Z)V", false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(-1, -1);
        mv.visitEnd();

    }

    private void defineWriteln(){
        //print string to the console with \n
        MethodVisitor mv = cw.visitMethod
                (Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "writeln", "(Ljava/lang/String;)V", null, null);
        mv.visitCode();
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(-1, -1);
        mv.visitEnd();
        //print int to the console with \n
        mv = cw.visitMethod
                (Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "writeln", "(I)V", null, null);
        mv.visitCode();
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitVarInsn(Opcodes.ILOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(-1, -1);
        mv.visitEnd();
        //print float to the console with \n
        mv = cw.visitMethod
                (Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "writeln", "(F)V", null, null);
        mv.visitCode();
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitVarInsn(Opcodes.FLOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(F)V", false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(-1, -1);
        mv.visitEnd();

        //print boolean to the console with \n
        mv = cw.visitMethod
                (Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "writeln", "(Z)V", null, null);
        mv.visitCode();
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitVarInsn(Opcodes.ILOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Z)V", false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(-1, -1);
        mv.visitEnd();

        //print nothing to the console with \n
        mv = cw.visitMethod
                (Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "writeln", "()V", null, null);
        mv.visitCode();
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "()V", false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(-1, -1);
        mv.visitEnd();



    }



    public void defineDefaultsMethods() {
        defineReadInt();
        defineReadFloat();
        defineReadString();
        //defineWriteInt();
        //defineWriteFloat();
        defineWrite();
        defineWriteln();
        defineLen();
        defineChr();
    }

    public byte[] generateBytecode() throws NoSuchFieldException, IllegalAccessException {
        cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, programName, null, "java/lang/Object", null);
        defineDefaultsMethods();

        MethodVisitor mainMethodWriter= cw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                "main", "([Ljava/lang/String;)V", null, null);

        codeGenerationVisitor = new CodeGenerationVisitor(cw,programName,path);

        // Generate the bytecode for the AST
        Iterator<ASTNode> it= program.iterator();
        while (it.hasNext()) {
            // Visit the node
            ASTNode node= it.next();
            node.accept(codeGenerationVisitor, table, mainMethodWriter);
        }

        //At the last call the main function
        mainMethodWriter.visitMethodInsn(INVOKESTATIC, programName, "main", "()V", false);

        mainMethodWriter.visitInsn(Opcodes.RETURN);
        mainMethodWriter.visitMaxs(-1, -1);
        mainMethodWriter.visitEnd();

        cw.visitEnd();
        byte[] bytecode = cw.toByteArray();

        try (FileOutputStream outputStream = new FileOutputStream(outputFilePath)){
            outputStream.write(bytecode);
            Files.write(Paths.get(outputFilePath), bytecode);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytecode;

        // Esegui il metodo main della classe generata
        // clazz.getMethod("main", String[].class).invoke(null, (Object) new String[0]);

    }





}
