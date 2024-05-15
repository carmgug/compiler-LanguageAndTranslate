package compiler.CodeGenerator;

import compiler.Exceptions.SemanticException.SemanticException;
import compiler.Parser.AST.ASTNode;
import compiler.Parser.AST.ASTNodes.*;
import compiler.Parser.AST.ASTNodes.Expressions.FunctionCall;
import compiler.Parser.AST.ASTNodes.Expressions.Type;
import compiler.Parser.AST.ASTNodes.Expressions.Types.VoidType;
import compiler.Parser.AST.ASTNodes.Expressions.VariableReference;
import compiler.SemanticAnalysis.SymbolTable.SymbolTable;
import compiler.SemanticAnalysis.Visitor.Visitor;
import org.objectweb.asm.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static org.objectweb.asm.Opcodes.*;

public class CodeGenerationVisitor  {



    private final ClassWriter cw;
    private final EvaluateVisitor evaluator;
    private int stack_index = 0;
    private String program_name;


    //To keep track of the type of the current procedure that we are visiting, instead of passing it as a parameter
    //to the visit method of the block, because when we encounter a return statement, i need to know the type of the return
    private Type type_of_current_procedure=null;

    public CodeGenerationVisitor(ClassWriter cw, String program_name, SymbolTable globalTable, SymbolTable structTable) {
        this.cw = cw;
        this.evaluator = new EvaluateVisitor(this,program_name,globalTable,structTable);
        this.program_name = program_name;
    }

    public void increment_stack_index(){
        stack_index++;
    }

    public int get_stack_index(){
        return stack_index;
    }

    public void visit(Constant constant, ScopesTable curr_scope, MethodVisitor mw){
        System.out.println("Visiting Constant");
        String costant_name=constant.getConstantName();
        FieldVisitor fw=cw.visitField(ACC_PRIVATE | ACC_FINAL, costant_name, getFieldType(constant.getType()), null, null);
        ExpressionStatement right_side=constant.getRight_side();
        right_side.accept(evaluator,curr_scope,mw);
        curr_scope.add(costant_name,-1,constant.getType());

        //Put the field as final


        switch (constant.getType().getNameofTheType()) {
            case "int":
                //Store the value in the stack as a static field
                //how to do it?
                //mw.visitVarInsn(ISTORE,stack_index);
                //how can i save the variable also in the stack?
                mw.visitFieldInsn(PUTSTATIC, program_name, costant_name, "I");
                break;
            case "float":
                //mw.visitVarInsn(Opcodes.FSTORE,stack_index);
                mw.visitFieldInsn(PUTSTATIC, program_name, costant_name, "F");
                break;
            case "bool":
                //mw.visitVarInsn(ISTORE,stack_index);
                mw.visitFieldInsn(PUTSTATIC, program_name, costant_name, "Z");
                break;
            case "string":
                //mw.visitVarInsn(Opcodes.ASTORE,stack_index);
                mw.visitFieldInsn(PUTSTATIC, program_name, costant_name, "Ljava/lang/String;");
                break;
        }

        fw.visitEnd();
    }

    public void visit(Struct struct, ScopesTable curr_scope, MethodVisitor mw){
        System.out.println("Visiting Struct");
        String struct_name=struct.getStructName();
        ArrayList<VariableDeclaration> fields=struct.getVariableDeclarations();

        //Create a new class
        ClassWriter struct_cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        struct_cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC , struct_name, null, "java/lang/Object", null);
        //For Each field create a field
        for (VariableDeclaration field:fields){
            struct_cw.visitField(Opcodes.ACC_PUBLIC, field.getNameOfTheVariable(), getFieldType(field.getType()), null, null);
        }

        //Add Constructor
        Type[] paramTypes = fields.stream().map(VariableDeclaration::getType).toArray(Type[]::new);
        //define descriptor
        String descriptor = constructorDescriptor(paramTypes);
        System.out.println(descriptor);
        MethodVisitor init = struct_cw.visitMethod(ACC_PUBLIC, "<init>", descriptor, null, null);
        init.visitCode();
        init.visitVarInsn(ALOAD, 0); // this
        init.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        int i = 1;
        for (VariableDeclaration field: fields) {
            init.visitVarInsn(ALOAD, 0);
            org.objectweb.asm.Type type = org.objectweb.asm.Type.getType(getFieldType(field.getType()));
            init.visitVarInsn(type.getOpcode(ILOAD), i);
            i += type.getSize();
            init.visitFieldInsn(PUTFIELD, struct_name, field.getNameOfTheVariable(), type.getDescriptor());
        }
        init.visitInsn(RETURN);
        init.visitMaxs(-1,-1);
        init.visitEnd();

        struct_cw.visitEnd();
        byte[] bytecode = struct_cw.toByteArray();
        System.out.println(Arrays.toString(bytecode) + " " + bytecode.length);

        try (FileOutputStream outputStream = new FileOutputStream("./" + program_name + ".class")) {
            outputStream.write(bytecode);
            Files.write(Paths.get(struct_name+".class"), bytecode);

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Struct "+struct_name+" created");
    }


    public void visit(GlobalVariable globalVariable, ScopesTable curr_scope, MethodVisitor mw){
        System.out.println("Visiting Constant");
        String variable_name= globalVariable.getNameOfTheVariable();
        FieldVisitor fw=cw.visitField(ACC_PRIVATE , variable_name, getFieldType(globalVariable.getType()), null, null);
        ExpressionStatement right_side=globalVariable.getValue();
        right_side.accept(evaluator,curr_scope,mw);
        curr_scope.add(variable_name,-1,globalVariable.getType());
        String descriptor = getFieldType(globalVariable.getType());
        mw.visitFieldInsn(PUTSTATIC, program_name, variable_name, descriptor);

        fw.visitEnd();
    }

    public void visit(Procedure procedure,ScopesTable curr_scope, MethodVisitor mw){
        System.out.println("Visiting Procedure");
        String procedure_name=procedure.getProcedureName();
        ArrayList<VariableDeclaration> parameters=procedure.getParameters_of_the_procedure();
        Block block = procedure.getBody();
        //Create a new curr_scope
        ScopesTable procedure_scopes_tables = new ScopesTable(curr_scope);
        type_of_current_procedure=procedure.getReturnType();

        //Create the method
        Type[] paramTypes = parameters.stream().map(VariableDeclaration::getType).toArray(Type[]::new);
        String descriptor = methodDescriptor(type_of_current_procedure,paramTypes);
        MethodVisitor procedure_visitor = cw.visitMethod(ACC_PUBLIC | ACC_STATIC, procedure_name, descriptor, null, null);
        procedure_visitor.visitCode();
        //Add the parameters to the curr_scope
        int orginal_stack_index = stack_index;
        stack_index = 0;
        for (VariableDeclaration parameter: parameters) {
            procedure_visitor.visitVarInsn(ALOAD, 0);
            org.objectweb.asm.Type type = org.objectweb.asm.Type.getType(getFieldType(parameter.getType()));
            procedure_visitor.visitVarInsn(type.getOpcode(ILOAD), stack_index);
            procedure_scopes_tables.add(parameter.getNameOfTheVariable(),stack_index,parameter.getType());
            stack_index += type.getSize();
        }

        //Visit the block
        block.accept(this,procedure_scopes_tables,procedure_visitor);

        procedure_visitor.visitMaxs(-1,-1);
        procedure_visitor.visitEnd();
        stack_index = orginal_stack_index;
        type_of_current_procedure=null;
    }

    public void visit(Block block, ScopesTable curr_scope, MethodVisitor mw){
        System.out.println("Visiting Block");
        ArrayList<ASTNode> statements=block.getStatements();
        for (int i = 0; i < block.getStatements().size(); i++) {
           statements.get(i).accept(this, curr_scope, mw);
        }
    }

    public void visit(IfStatement ifStatement, ScopesTable curr_scope, MethodVisitor mw){
        System.out.println("Visiting IfStatement");
        ExpressionStatement condition = ifStatement.getIfCondition();
        Block then_block = ifStatement.getIfBlock();
        condition.accept(evaluator,curr_scope,mw);
        //If the condition is true, go to the then block
        //if the condition is false, skip block
        Label if_start = new Label();
        Label if_end = new Label();
        mw.visitJumpInsn(IFEQ, if_end);
        mw.visitLabel(if_start);
        then_block.accept(this,curr_scope,mw);
        mw.visitLabel(if_end);
    }

    public void visit(IfElseStatement ifElseStatement,ScopesTable curr_scope,MethodVisitor mw){
        System.out.println("Visiting IfElseStatement");
        ExpressionStatement condition = ifElseStatement.getIfCondition();
        Block then_block = ifElseStatement.getIfBlock();
        Block else_block = ifElseStatement.getElse_block();
        condition.accept(evaluator,curr_scope,mw);
        Label if_start = new Label();
        Label else_start = new Label();
        mw.visitJumpInsn(IFEQ, else_start);
        mw.visitLabel(if_start);
        then_block.accept(this,curr_scope,mw);
        mw.visitLabel(else_start);
        else_block.accept(this,curr_scope,mw);
    }




    public void visit(VariableInstantiation variableInstantiation, ScopesTable curr_scope,MethodVisitor mw){
        System.out.println("Visiting VariableInstantiation");
        //Get name of the variable
        String var_name = variableInstantiation.getNameOfTheVariable();
        //Get type of the variable
        Type type = variableInstantiation.getType();
        //Evaluate right_side
        ExpressionStatement right_side = variableInstantiation.getRight_side();
        right_side.accept(evaluator,curr_scope,mw);
        org.objectweb.asm.Type type_op = org.objectweb.asm.Type.getType(getFieldType(type));
        mw.visitVarInsn(type_op.getOpcode(ISTORE),stack_index);
        curr_scope.add(var_name,stack_index,type);
        stack_index+=type_op.getSize();
    }

    public void visit(VariableDeclaration variableDeclaration, ScopesTable curr_scope, MethodVisitor mw){
        System.out.println("Visiting VariableDeclaration");
        //Get name of the variable
        String var_name = variableDeclaration.getNameOfTheVariable();
        //Get type of the variable
        Type type = variableDeclaration.getType();
        //Evaluate right_side
        org.objectweb.asm.Type type_op = org.objectweb.asm.Type.getType(getFieldType(type));
        curr_scope.add(var_name,stack_index,type);
        stack_index+=type_op.getSize();
    }

    public void visit(VariableAssigment variableAssigment,ScopesTable curr_scope, MethodVisitor mw){
        System.out.println("Visiting VariableAssigment");
        //Evaluate right_side
        variableAssigment.getRight_side().accept(evaluator,curr_scope,mw);
        //Assign the value to the variable
        variableAssigment.getVariable().accept(this,curr_scope,mw);
    }

    public void visit(VariableReference variableReference,ScopesTable curr_scope,MethodVisitor mw){
        System.out.println("Visiting VariableReference");
        //Recupera il nome della variabile
        String var_name = variableReference.getIdentifier();
        Type type = curr_scope.getType(var_name);
        //Recupera l'indice della variabile
        int index = curr_scope.getIndex(var_name);
        //se index è -1 allora parliamo di una variabile globale/final
        //altrimenti è una variabile locale
        storeValueInTheVariable(index, type, mw);
        //how to see the type of var on the stack at specified index
    }

    private void storeValueInTheVariable(int index,Type type,MethodVisitor mw){
        switch (type.getNameofTheType()) {
            case "int":
                if(index==-1) {
                    mw.visitFieldInsn(GETSTATIC, program_name, "I", "I");
                    return;
                }
                mw.visitVarInsn(ISTORE, index);
                return;
            case "bool":
                if(index==-1) {
                    mw.visitFieldInsn(GETSTATIC, program_name, "Z", "Z");
                    return;
                }
                mw.visitVarInsn(ISTORE, index);
                return;
            case "float":
                if(index==-1) {
                    mw.visitFieldInsn(GETSTATIC, program_name, "F", "F");
                    return;
                }
                mw.visitVarInsn(FSTORE, index);
                return;
            case "string":
                if(index==-1) {
                    mw.visitFieldInsn(GETSTATIC, program_name, "Ljava/lang/String;", "Ljava/lang/String;");
                    return;
                }
                mw.visitVarInsn(ASTORE, index);
                break;
            default:
                mw.visitVarInsn(ASTORE, index);
        }
    }

    public void visit(ReturnStatement returnStatement, ScopesTable currScope, MethodVisitor mw) {
        System.out.println("Visiting ReturnStatement");
        ExpressionStatement expressionStatement = returnStatement.getExpression();
        if(expressionStatement==null){
            mw.visitInsn(Opcodes.RETURN);
            return;
        }
        expressionStatement.accept(evaluator,currScope,mw);
        org.objectweb.asm.Type type = org.objectweb.asm.Type.getType(getFieldType(type_of_current_procedure));
        mw.visitInsn(type.getOpcode(IRETURN));

    }

    private String methodDescriptor(Type returnType, Type[] paramTypes) {
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

    private String constructorDescriptor(Type[] paramTypes) {
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        for (Type paramType : paramTypes) {
            sb.append(getFieldType(paramType));

        }
        sb.append(')');
        sb.append("V");
        return sb.toString();
    }
    private String getFieldType(Type type){
        System.out.println(type.getNameofTheType());
        switch (type.getNameofTheType()) {
            case "int":
                return "I";
            case "float":
                return "F";
            case "bool":
                return "Z";
            case "string":
                return "Ljava/lang/String;";
            case "void":
                return "V";
            default:
                return "L"+type.getNameofTheType()+";";
        }
    }


    public void visit(FunctionCall functionCall, ScopesTable curr_scope, MethodVisitor mw) {
        System.out.println("Visiting FunctionCall");
        //How to call a function e pass parameters
        //Take the info about the function from the global table
        //Procedures are stored in the global table
        //Visit the parameters
        if (functionCall.isConstructor()){
            mw.visitTypeInsn(NEW, functionCall.getReturnType().getNameofTheType());
            mw.visitInsn(DUP);
        }
        boolean empty = functionCall.getParameters().isEmpty();
        for(ExpressionStatement param:functionCall.getParameters()){
            param.accept(evaluator,curr_scope,mw);
        }
        //the paramType can be also empty

        Type[] paramType= empty ?
                null :
                functionCall.getParametersType().toArray(new Type[0]);
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

    /*
        Rivedere gli opearatori, non sono sicuro che siano corretti
        ForStatement, WhileStatement
     */
}
