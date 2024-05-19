package compiler.CodeGenerator;

import compiler.Parser.AST.ASTNodes.Expressions.Type;
import compiler.Parser.AST.ASTNodes.Expressions.Types.ArrayStructType;
import compiler.Parser.AST.ASTNodes.Expressions.Types.ArrayType;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static org.objectweb.asm.Opcodes.*;

public class CodeGenerationUtility {
    //is a utility class that contains methods that are used by the code generation visitor


    /*
        *  This method is used to store a value in a variable
        *  @param index: the index of the variable in the stack
        *  @param type: the type of the variable
        *  @param mw: the method visitor
        *  @param program_name: the name of the program
     */
    public static void storeValueInTheVariable(String var_name,int index,Type type,MethodVisitor mw,String program_name){
        switch (type.getNameofTheType()) {
            case "int":
                if(index==-1) {
                    mw.visitFieldInsn(PUTSTATIC, program_name, var_name, getFieldType(type));
                    return;
                }
                mw.visitVarInsn(ISTORE, index);
                return;
            case "bool":
                if(index==-1) {
                    mw.visitFieldInsn(PUTSTATIC, program_name, var_name, getFieldType(type));
                    return;
                }
                mw.visitVarInsn(ISTORE, index);
                return;
            case "float":
                if(index==-1) {
                    mw.visitFieldInsn(PUTSTATIC, program_name, var_name, getFieldType(type));
                    return;
                }
                mw.visitVarInsn(FSTORE, index);
                return;
            case "string":
                if(index==-1) {
                    mw.visitFieldInsn(PUTSTATIC, program_name, var_name, getFieldType(type));
                    return;
                }
                mw.visitVarInsn(ASTORE, index);
                break;
            default:
                //Complex Type (Struct)
                if(index==-1) {
                    mw.visitFieldInsn(PUTSTATIC, program_name, var_name, getFieldType(type));
                    return;
                }
                mw.visitVarInsn(ASTORE, index);
        }
    }

    public static void loadVariable(int index, Type type, MethodVisitor mw,String var_name,String program_name){

        //Could be an Array
        if(type instanceof ArrayType || type instanceof ArrayStructType){
            if(index==-1) {
                mw.visitFieldInsn(GETSTATIC, program_name, var_name, CodeGenerationUtility.getFieldType(type));
                return;
            }
            mw.visitVarInsn(ALOAD, index);
            return;
        }
        switch (type.getTokenType()) {
            case IntType:
                if(index==-1) {
                    mw.visitFieldInsn(GETSTATIC, program_name, var_name, "I");
                    return;
                }
                mw.visitVarInsn(ILOAD, index);
                return;
            case BoolType:
                if(index==-1) {
                    mw.visitFieldInsn(GETSTATIC, program_name, var_name, "Z");
                    return;
                }
                mw.visitVarInsn(ILOAD, index);
                return;
            case FloatType:
                if(index==-1) {
                    mw.visitFieldInsn(GETSTATIC, program_name, var_name, "F");
                    return;
                }
                mw.visitVarInsn(FLOAD, index);
                return;
            case StringType:
                if(index==-1) {
                    mw.visitFieldInsn(GETSTATIC, program_name, var_name, "Ljava/lang/String;");
                    return;
                }
                mw.visitVarInsn(ALOAD, index);
                break;
            default:
                //Ok it is a struct so like this
                //p.field
                if(index==-1) {
                    mw.visitFieldInsn(GETSTATIC, program_name, var_name, "L"+type.getNameofTheType()+";");
                    return;
                }
                mw.visitVarInsn(ALOAD, index);
        }
    }

    /*
        *  This method is used to initialize an array of a specific type and size
     */
    public static int initArray(Type type, int size,MethodVisitor mw){
        mw.visitLdcInsn(size);
        switch(type.getTokenType()){
            case IntType:
                mw.visitIntInsn(NEWARRAY, T_INT);
                return IASTORE;
            case BoolType:
                mw.visitIntInsn(NEWARRAY, T_BOOLEAN);
                return BASTORE;
            case FloatType:
                mw.visitIntInsn(NEWARRAY, T_FLOAT);
                return FASTORE;
            case StringType:
                mw.visitTypeInsn(ANEWARRAY, "java/lang/String");
                return AASTORE;
            default:
                //Complex Type (Struct)
                mw.visitTypeInsn(ANEWARRAY, type.getNameofTheType());
                return AASTORE;
        }
    }

/*
        *  This method is used to load a value of an array
        *  @param type: the type of the array
        *  @param mw: the method visitor
     */
    public static void loadValueOfArray(Type type,MethodVisitor mw){
        switch (type.getTokenType()) {
            case IntType:
                mw.visitInsn(IALOAD);
                break;
            case BoolType:
                mw.visitInsn(BALOAD);
                break;
            case FloatType:
                mw.visitInsn(FALOAD);
                break;
            case StringType:
                mw.visitInsn(AALOAD);
                break;
            default:
                mw.visitInsn(AALOAD);
        }

    }


    /*
        @param returnType: the return type of a method
        @param paramTypes: the types of the parameters of a method
        @return the method descriptor
     */
    public static String methodDescriptor(Type returnType, Type[] paramTypes) {
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        if (paramTypes != null) {
            for (Type paramType : paramTypes) {
                sb.append(getFieldType(paramType));
            }
        }
        sb.append(')');
        sb.append(getFieldType(returnType));
        return sb.toString();
    }

    /*
        @param paramTypes: the types of the parameters of a constructor
        @return the constructor descriptor
     */
    public static String constructorDescriptor( Type[] paramTypes) {
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        for (Type paramType : paramTypes) {
            sb.append(getFieldType(paramType));
        }
        sb.append(')');
        sb.append('V');
        return sb.toString();
    }

    /*
        @param type: the type of the field
        @return the field type descriptor
     */
    public static String getFieldType(Type type){
        switch (type.getNameofTheType()) {
            case "int":
                if (type instanceof ArrayType){
                    return "[I";
                }
                return "I";
            case "float":
                if (type instanceof ArrayType){
                    return "[F";
                }
                return "F";
            case "bool":
                if (type instanceof ArrayType){
                    return "[Z";
                }
                return "Z";
            case "string":
                if (type instanceof ArrayType){
                    return "[Ljava/lang/String;";
                }
                return "Ljava/lang/String;";
            case "void":
                return "V";
            default:
                if (type instanceof ArrayStructType){
                    return "[L"+type.getNameofTheType()+";";
                }
                return "L"+type.getNameofTheType()+";";
        }
    }


    /*
        For evry struct we need to define len method for Struct[] Array
     */
    public static void defineLenArrayStruct(ClassWriter cw, String programName, String structName){
        //define len method for array of Struct
        //input array return an integer
        //is public and static
        MethodVisitor mv = cw.visitMethod
                (Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "len", "([L"+structName+";)I", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitInsn(Opcodes.ARRAYLENGTH);
        mv.visitInsn(Opcodes.IRETURN);
        mv.visitMaxs(-1, -1);
        mv.visitEnd();

    }



}
