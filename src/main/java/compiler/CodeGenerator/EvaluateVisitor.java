package compiler.CodeGenerator;

import compiler.Lexer.Symbol;
import compiler.Lexer.Token;
import compiler.Parser.AST.ASTNodes.Block;
import compiler.Parser.AST.ASTNodes.ExpressionStatement;
import compiler.Parser.AST.ASTNodes.Expressions.*;
import compiler.Parser.AST.ASTNodes.Expressions.NegationNodes.ArithmeticNegationNode;
import compiler.Parser.AST.ASTNodes.Expressions.NegationNodes.BooleanNegationNode;
import compiler.Parser.AST.ASTNodes.Expressions.Type;
import compiler.Parser.AST.ASTNodes.Expressions.Types.ArrayStructType;
import compiler.Parser.AST.ASTNodes.Expressions.Types.ArrayType;
import compiler.Parser.AST.ASTNodes.VariableDeclaration;
import compiler.Parser.AST.ASTNodes.VariableInstantiation;
import compiler.SemanticAnalysis.SymbolTable.SymbolTable;
import compiler.SemanticAnalysis.SymbolTable.SymbolTableEntry;
import compiler.SemanticAnalysis.SymbolTable.SymbolTableValues.SymbolTableProceduresEntry;
import compiler.SemanticAnalysis.SymbolTable.SymbolTableValues.SymbolTableType;
import org.objectweb.asm.*;
import org.objectweb.asm.Opcodes.*;

import java.util.ArrayList;

import static org.objectweb.asm.Opcodes.IOR;
import static org.objectweb.asm.Opcodes.IREM;
import static org.objectweb.asm.Opcodes.*;

public class EvaluateVisitor {

    private CodeGenerationVisitor codeGeneratorVisitor;
    private SymbolTable globalTable;
    private String program_name;
    private boolean visiting_struct = false;
    private Type struct_type_to_visit;
    private Type struct_field_type_to_visit;
    private boolean visiting_binary_operation = false;
    private Type binary_operation;
    private BinaryExpression visiting_binaryExpression;




    public EvaluateVisitor(CodeGenerationVisitor codeGeneratorVisitor, String program_name, SymbolTable globalTable,SymbolTable structTable){
        this.codeGeneratorVisitor = codeGeneratorVisitor;
        this.program_name = program_name;
        this.globalTable = globalTable;
    }
    public void visit(Value value, ScopesTable curr_scope, MethodVisitor mw){
        //Il value può essere un intero, un booleano, una stringa o un carattere
        String v = value.getValue();
        if(visiting_binary_operation){
            switch(binary_operation.getTokenType()){
                case IntType:
                    mw.visitLdcInsn(Integer.parseInt(v));
                    break;
                case FloatType:
                    mw.visitLdcInsn(Float.parseFloat(v));
                    break;
                case BoolType:
                    mw.visitLdcInsn(Boolean.parseBoolean(v));
                    break;
                case StringType:
                    mw.visitLdcInsn(v);
                    break;
            }
            return;
        }

        switch(value.getSymbol().getType()){
            case IntNumber:
                //Inserire il valore intero nella pila
                mw.visitLdcInsn(Integer.parseInt(v));
                break;
            case BooleanValue:
                //Inserire il valore booleano nella pila
                mw.visitLdcInsn(Boolean.parseBoolean(v));
                break;
            case String:
                //Inserire il valore char nella pila
                mw.visitLdcInsn(v);
                break;
            case FloatNumber:
                //Inserire il valore stringa nella pila
                mw.visitLdcInsn(Float.parseFloat(v));
                break;
        }

    }

    public void visit(VariableReference variableReference, ScopesTable curr_scope, MethodVisitor mw){


        //Recupera il nome della variabile
        String var_name = variableReference.getIdentifier();
        Type type = curr_scope.getType(var_name);
        //Recupera l'indice della variabile
        if (visiting_struct){
            //if i am visiting a struct so i need to use GETFIELD instead of ILOAD
            //maybe we have struct.field.field so we need to know the type of the second field how to know?
            mw.visitFieldInsn(GETFIELD, struct_type_to_visit.getNameofTheType(), var_name, getFieldType(struct_field_type_to_visit));

            //mw.visitTypeInsn(CHECKCAST, getFieldType(struct_field_type_to_visit));
            return;
        }
        int index = curr_scope.getIndex(var_name);

        //se index è -1 allora parliamo di una variabile globale/final
        //altrimenti è una variabile locale
        loadVariable(index, type, mw,var_name);
        //how to see the type of var on the stack at specified index
        //Store the value of the variable in the stack
        //mw.visitVarInsn(ASTORE,codeGeneratorVisitor.get_stack_index());

    }

    public void visit(FunctionCall functionCall, ScopesTable curr_scope, MethodVisitor mw){
        //How to call a function e pass parameters
        //Take the info about the function from the global table
        //Procedures are stored in the global table
        //Visit the parameters
        if (functionCall.isConstructor()){
            mw.visitTypeInsn(NEW, functionCall.getReturnType().getNameofTheType());
            mw.visitInsn(DUP);
        }

        for(ExpressionStatement param:functionCall.getParameters()){
            param.accept(this,curr_scope,mw);
        }
        Type[] paramType= functionCall.getParametersType().toArray(new Type[0]);
        //Get the return type of the function
        if(!functionCall.isConstructor()) {
            //If it is not a constructor
            //Get the return type of the function
            Type returnType = functionCall.getReturnType();
            String descriptor = methodDescriptor(returnType, paramType);
            //Call the function
            mw.visitMethodInsn(INVOKESTATIC, program_name, functionCall.getFunctionName(), descriptor, false);
        }
        else{
            //If it is a constructor
            //Get the return type of the function
            String descriptor = constructorDescriptor( paramType);
            //i need to create a new object
            //Create a new object
            mw.visitMethodInsn(INVOKESPECIAL, functionCall.getFunctionName(), "<init>", descriptor, false);
        }


    }

    public void visit(StructAccess structAccess,ScopesTable curr_scope,MethodVisitor mw){
        //ok take the variable that is at the left of the dot


        structAccess.getLeftPart().accept(this,curr_scope,mw);

        visiting_struct = true;
        struct_type_to_visit = structAccess.getLeftType();
        struct_field_type_to_visit = structAccess.getRightType();
        //Ok now if the right part is another struct access or a array, the right part is the left part of the deeper struct access;
        if(structAccess.getRightPart() instanceof StructAccess){
            //if the left part is a struct access so the right part is the left part of the deeper struct access
            struct_field_type_to_visit = ((StructAccess)structAccess.getRightPart()).getLeftType();
            if(((StructAccess) structAccess.getRightPart()).getLeftPart() instanceof ArrayAccess){
                struct_field_type_to_visit = new ArrayStructType(struct_field_type_to_visit.getSymbol());
            }
        }

        //Ok now i have the object on the stack, now i need to access the field
        //Now access the field
        //structAccess.getRightPart().accept(this,curr_scope,mw);
        //ok now visit the right part but instead of using curr_scop you need to use GETFIELD;
        structAccess.getRightPart().accept(this,curr_scope,mw);
        visiting_struct = false;
        struct_type_to_visit = null;
        struct_field_type_to_visit = null;

    }

    public void visit(ArrayValueDeclaration arrayValueDeclaration, ScopesTable curr_scope, MethodVisitor mw){
        //Get the type of the array
        Type type = arrayValueDeclaration.getType();
        int size = arrayValueDeclaration.getValues().size();

        //Get the size of the
        //throw new RuntimeException("Not implemented yet");
        //Create the array
        //Before Create load the size of the array
        mw.visitLdcInsn(size);
        int opStoreCode=initArray(type,mw);
        //ok now the array is on the stack like this type[] array=new type[size];
        //mw.visitVarInsn(ASTORE,stack_index);
        int i=0;
        for(ExpressionStatement value:arrayValueDeclaration.getValues()){
            //Load the array
            mw.visitInsn(DUP);
            mw.visitLdcInsn(i); //load index
            value.accept(this,curr_scope,mw); //load value
            mw.visitInsn(opStoreCode); //store the value
            i++;

        }
    }

    public void visit(ArrayAccess arrayAccess, ScopesTable curr_scope, MethodVisitor mw){
        //Go to the left part to visit array
        arrayAccess.getArray().accept(this,curr_scope,mw);
        //you have loaded the array on the stack

        //Load the index of the array
        boolean changed = visiting_struct;
        //Type struct_type = struct_type_to_visit;
        //Type struct_field_type = struct_field_type_to_visit;
        if(visiting_struct) {visiting_struct = false;


            changed=true;
        } //the index is not a field of a struct so the variable need to be load from the curr_scope
        arrayAccess.getIndex().accept(this,curr_scope,mw);
        if(changed) {visiting_struct = true;changed=false;}
        //Load the value of the array
        loadValueOfArray(arrayAccess.getType(),mw);
    }

    private int initArray(Type type,MethodVisitor mw){
        switch(type.getTokenType()){
            case IntType:
                mw.visitIntInsn(NEWARRAY, T_INT);
                return IASTORE;
            case BoolType:
                mw.visitIntInsn(NEWARRAY, T_BOOLEAN);
                return IASTORE;
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

    private String methodDescriptor(Type returnType, Type[] paramTypes) {
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        for (Type paramType : paramTypes) {
            sb.append(getFieldType(paramType));
        }
        sb.append(')');
        sb.append(getFieldType(returnType));
        return sb.toString();
    }

    private String constructorDescriptor( Type[] paramTypes) {
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        for (Type paramType : paramTypes) {
            sb.append(getFieldType(paramType));
        }
        sb.append(')');
        sb.append('V');
        return sb.toString();
    }

    private String getFieldType(Type type){
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

    private void loadValueOfArray(Type type,MethodVisitor mw){
        switch (type.getTokenType()) {
            case IntType:
                mw.visitInsn(IALOAD);
                break;
            case BoolType:
                mw.visitInsn(IALOAD);
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

    private void loadVariable(int index, Type type, MethodVisitor mw,String var_name){

        //Could be an Array
        if(type instanceof ArrayType || type instanceof ArrayStructType){
            if(index==-1) {
                mw.visitFieldInsn(GETSTATIC, program_name, var_name, getFieldType(type));
                //mw.visitTypeInsn(Opcodes.CHECKCAST, getFieldType(type));
                return;
            }
            mw.visitVarInsn(ALOAD, index);
            //mw.visitTypeInsn(Opcodes.CHECKCAST, getFieldType(type));

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

    public void visit(BinaryExpression binaryExpression,ScopesTable curr_scope, MethodVisitor mw){

        //Visita il figlio sinistro
        binaryExpression.getLeft().accept(this,curr_scope,mw);

        //explicit cast
        //Visita il figlio destro
        binaryExpression.getRight().accept(this,curr_scope,mw);


        //mw.visitTypeInsn(CHECKCAST, getFieldType(binaryExpression.getResultType()));

        Type type_value_left=binaryExpression.getLeftType();
        Type type_value_right=binaryExpression.getRightType();
        org.objectweb.asm.Type type_op= org.objectweb.asm.Type.getType(getFieldType(type_value_left));

        //Effettua l'operazione richiesta
        switch(binaryExpression.getOperator().getOperator()){
            case "+":
                if(binaryExpression.getResultType().getTokenType()==Token.IntType){
                    mw.visitInsn(IADD);
                }
                else{
                    mw.visitInsn(FADD);
                }
                break;
            case "-":
                if(binaryExpression.getResultType().getTokenType()==Token.IntType){
                    mw.visitInsn(ISUB);
                }
                else{
                    mw.visitInsn(FSUB);
                }
                break;
            case "*":
                if(binaryExpression.getResultType().getTokenType()==Token.IntType){
                    mw.visitInsn(IMUL);
                }
                else{
                    mw.visitInsn(FMUL);
                }
                break;
            case "/":
                if(binaryExpression.getResultType().getTokenType()==Token.IntType){
                    mw.visitInsn(IDIV);
                }
                else{
                    mw.visitInsn(FDIV);
                }
                break;
            case "%":
                if(binaryExpression.getResultType().getTokenType()==Token.IntType){
                    mw.visitInsn(IREM);
                }
                else{
                    mw.visitInsn(FREM);
                }
                break;
            case "==":
                //if equal return true else false
                Label startLabel = new Label();
                Label endLabel = new Label();
                //if_icmpeq compares the two values on the top of the stack and if they are equal it jumps to the label
                //i dont know if is a boolean or an int or a float so i need to check the type
                if (type_value_left.getTokenType()==Token.IntType || type_value_left.getTokenType()==Token.BoolType) {
                    mw.visitJumpInsn(type_op.getOpcode(IF_ICMPEQ), startLabel);
                    mw.visitInsn(ICONST_0);
                    mw.visitJumpInsn(GOTO, endLabel);
                    mw.visitLabel(startLabel);
                    mw.visitInsn(ICONST_1);
                    mw.visitLabel(endLabel);
                }
                else if (type_value_left.getTokenType()==Token.FloatType) {
                    mw.visitInsn(FCMPL);
                    mw.visitJumpInsn(IFEQ, startLabel);
                    mw.visitInsn(ICONST_0);
                    mw.visitJumpInsn(GOTO, endLabel);
                    mw.visitLabel(startLabel);
                    mw.visitInsn(ICONST_1);
                    mw.visitLabel(endLabel);
                }
                else  {
                    //string and struct
                    throw new RuntimeException("Not implemented yet");
                }


                break;
            case "!=":
                //if not equal return true else false
                Label startLabel_2=new Label();
                Label endLabel_2=new Label();

                if (type_value_left.getTokenType()==Token.IntType || type_value_left.getTokenType()==Token.BoolType) {
                    mw.visitJumpInsn(IF_ICMPNE,startLabel_2);
                    mw.visitInsn(ICONST_0);
                    mw.visitJumpInsn(GOTO,endLabel_2);
                    mw.visitLabel(startLabel_2);
                    mw.visitInsn(ICONST_1);
                    mw.visitLabel(endLabel_2);
                }
                else if (type_value_left.getTokenType()==Token.FloatType) {
                    mw.visitInsn(FCMPL);
                    mw.visitJumpInsn(IFNE, startLabel_2);
                    mw.visitInsn(ICONST_0);
                    mw.visitJumpInsn(GOTO, endLabel_2);
                    mw.visitLabel(startLabel_2);
                    mw.visitInsn(ICONST_1);
                    mw.visitLabel(endLabel_2);
                }
                else  {
                    //string and struct
                    throw new RuntimeException("Not implemented yet");
                }
                break;
            case "<":
                //if less than return true else false
                Label startLabel_3=new Label();
                Label endLabel_3=new Label();
                if (type_value_left.getTokenType()==Token.IntType || type_value_left.getTokenType()==Token.BoolType) {
                    mw.visitJumpInsn(IF_ICMPLT,startLabel_3);
                    mw.visitInsn(ICONST_0);
                    mw.visitJumpInsn(GOTO,endLabel_3);
                    mw.visitLabel(startLabel_3);
                    mw.visitInsn(ICONST_1);
                    mw.visitLabel(endLabel_3);
                }
                else if (type_value_left.getTokenType()==Token.FloatType) {
                    mw.visitInsn(FCMPL);
                    mw.visitJumpInsn(IFLT, startLabel_3);
                    mw.visitInsn(ICONST_0);
                    mw.visitJumpInsn(GOTO, endLabel_3);
                    mw.visitLabel(startLabel_3);
                    mw.visitInsn(ICONST_1);
                    mw.visitLabel(endLabel_3);
                }else  {
                    //string and struct
                    throw new RuntimeException("Not implemented yet");
                }
                break;
            case ">":
                //if greater than return true else false
                Label startLabel_4=new Label();
                Label endLabel_4=new Label();
                if (type_value_left.getTokenType()==Token.IntType || type_value_left.getTokenType()==Token.BoolType) {
                    mw.visitJumpInsn(IF_ICMPGT,startLabel_4);
                    mw.visitInsn(ICONST_0);
                    mw.visitJumpInsn(GOTO,endLabel_4);
                    mw.visitLabel(startLabel_4);
                    mw.visitInsn(ICONST_1);
                    mw.visitLabel(endLabel_4);
                }
                else if (type_value_left.getTokenType()==Token.FloatType) {
                    mw.visitInsn(FCMPL);
                    mw.visitJumpInsn(IFGT, startLabel_4);
                    mw.visitInsn(ICONST_0);
                    mw.visitJumpInsn(GOTO, endLabel_4);
                    mw.visitLabel(startLabel_4);
                    mw.visitInsn(ICONST_1);
                    mw.visitLabel(endLabel_4);
                }else  {
                    //string and struct
                    throw new RuntimeException("Not implemented yet");
                }
                break;
            case ">=":
                //if greater than or equal return true else false
                Label startLabel_5=new Label();
                Label endLabel_5=new Label();
                if (type_value_left.getTokenType()==Token.IntType || type_value_left.getTokenType()==Token.BoolType) {
                    mw.visitJumpInsn(IF_ICMPGE,startLabel_5);
                    mw.visitInsn(ICONST_0);
                    mw.visitJumpInsn(GOTO,endLabel_5);
                    mw.visitLabel(startLabel_5);
                    mw.visitInsn(ICONST_1);
                    mw.visitLabel(endLabel_5);
                }
                else if (type_value_left.getTokenType()==Token.FloatType) {
                    mw.visitInsn(FCMPL);
                    mw.visitJumpInsn(IFGE, startLabel_5);
                    mw.visitInsn(ICONST_0);
                    mw.visitJumpInsn(GOTO, endLabel_5);
                    mw.visitLabel(startLabel_5);
                    mw.visitInsn(ICONST_1);
                    mw.visitLabel(endLabel_5);
                }else {
                    //string and struct
                    throw new RuntimeException("Not implemented yet");
                }

                break;
            case "<=":

                //if less than or equal return true else false
                Label startLabel_6=new Label();
                Label endLabel_6=new Label();
                if (type_value_left.getTokenType()==Token.IntType || type_value_left.getTokenType()==Token.BoolType) {
                    mw.visitJumpInsn(IF_ICMPLE,startLabel_6);
                    mw.visitInsn(ICONST_0);
                    mw.visitJumpInsn(GOTO,endLabel_6);
                    mw.visitLabel(startLabel_6);
                    mw.visitInsn(ICONST_1);
                    mw.visitLabel(endLabel_6);
                }
                else if (type_value_left.getTokenType()==Token.FloatType) {
                    mw.visitInsn(FCMPL);
                    mw.visitJumpInsn(IFLE, startLabel_6);
                    mw.visitInsn(ICONST_0);
                    mw.visitJumpInsn(GOTO, endLabel_6);
                    mw.visitLabel(startLabel_6);
                    mw.visitInsn(ICONST_1);
                    mw.visitLabel(endLabel_6);
                }else {
                    //string and struct
                    throw new RuntimeException("Not implemented yet");
                }
                break;
            case "&&":
                mw.visitInsn(IAND);
                break;
            case "||":
                mw.visitInsn(IOR);
                break;
        }


    }


    public void visit(ArithmeticNegationNode arithmeticNegationNode, ScopesTable currScope, MethodVisitor mw) {
        //Visita il figlio
        arithmeticNegationNode.getExpression().accept(this, currScope, mw);
        //Effettua la negazione aritmetica
        if(arithmeticNegationNode.getType().getTokenType()==Token.IntType){
            mw.visitInsn(INEG);
        }
        else{
            mw.visitInsn(FNEG);
        }
    }

    public void visit(BooleanNegationNode booleanNegationNode,ScopesTable currScope,MethodVisitor mw){
        //Visita il figlio
        booleanNegationNode.getExpression().accept(this,currScope,mw);
        //Effettua la negazione booleana
        mw.visitInsn(ICONST_1);
        mw.visitInsn(IXOR);
    }
}
