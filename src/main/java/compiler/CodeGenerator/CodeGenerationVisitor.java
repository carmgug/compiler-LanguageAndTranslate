package compiler.CodeGenerator;

import compiler.Lexer.Symbol;
import compiler.Lexer.Token;
import compiler.Parser.AST.ASTNode;
import compiler.Parser.AST.ASTNodes.*;
import compiler.Parser.AST.ASTNodes.Expressions.*;
import compiler.Parser.AST.ASTNodes.Expressions.Type;
import compiler.Parser.AST.ASTNodes.Expressions.Types.ArrayStructType;
import compiler.Parser.AST.ASTNodes.Expressions.Types.ArrayType;
import compiler.SemanticAnalysis.SymbolTable.SymbolTable;
import org.objectweb.asm.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import static compiler.CodeGenerator.CodeGenerationUtility.constructorDescriptor;
import static compiler.CodeGenerator.CodeGenerationUtility.getFieldType;
import static compiler.CodeGenerator.CodeGenerationUtility.methodDescriptor;

import static org.objectweb.asm.Opcodes.*;

public class CodeGenerationVisitor  {

    private final ClassWriter cw;
    private final EvaluateVisitor evaluator;
    private final String program_name;
    private final String path;
    private int stack_index = 0;




    //To keep track of the type of the current procedure that we are visiting, instead of passing it as a parameter
    //to the visit method of the block, because when we encounter a return statement, i need to know the type of the return
    private Type type_of_current_procedure=null;
    private boolean added_return = false;

    public CodeGenerationVisitor(ClassWriter cw, String program_name, String path) {
        this.cw = cw;
        this.evaluator = new EvaluateVisitor(this,program_name);
        this.program_name = program_name;
        this.path= path;
    }


    public void visit(Constant constant, ScopesTable curr_scope, MethodVisitor mw){
        String costant_name=constant.getConstantName();
        FieldVisitor fw=cw.visitField(ACC_PRIVATE | ACC_STATIC |ACC_FINAL, costant_name, getFieldType(constant.getType()), null, null);
        ExpressionStatement right_side=constant.getRight_side();
        right_side.accept(evaluator,curr_scope,mw);
        curr_scope.add(costant_name,-1,constant.getType());
        String descriptor = getFieldType(constant.getType());
        mw.visitFieldInsn(PUTSTATIC, program_name, costant_name, descriptor);
        fw.visitEnd();
    }

    public void visit(Struct struct, ScopesTable curr_scope, MethodVisitor mw){
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

        try (FileOutputStream outputStream = new FileOutputStream(path + struct_name + ".class")) {
            outputStream.write(bytecode);
            Files.write(Paths.get(path+struct_name+".class"), bytecode);

        } catch (IOException e) {
            e.printStackTrace();
        }

        //add the method len for the struct
        CodeGenerationUtility.defineLenArrayStruct(cw,program_name,struct_name);

    }

    public void visit(GlobalVariable globalVariable, ScopesTable curr_scope, MethodVisitor mw){
        String variable_name= globalVariable.getNameOfTheVariable();
        FieldVisitor fw=cw.visitField(ACC_PRIVATE | ACC_STATIC, variable_name, getFieldType(globalVariable.getType()), null, null);
        ExpressionStatement right_side=globalVariable.getValue();
        right_side.accept(evaluator,curr_scope,mw);
        curr_scope.add(variable_name,-1,globalVariable.getType());
        String descriptor = getFieldType(globalVariable.getType());
        mw.visitFieldInsn(PUTSTATIC, program_name, variable_name, descriptor);
        fw.visitEnd();
    }

    public void visit(Procedure procedure,ScopesTable curr_scope, MethodVisitor mw){
        String procedure_name=procedure.getProcedureName();
        ArrayList<VariableDeclaration> parameters=procedure.getParameters_of_the_procedure();
        Block block = procedure.getBody();
        //Create a new curr_scope
        ScopesTable procedure_scopes_tables = new ScopesTable(curr_scope);
        type_of_current_procedure=procedure.getReturnType();
        //Create the method
        Type[] paramTypes = parameters.stream().map(VariableDeclaration::getType).toArray(Type[]::new);
        String descriptor = CodeGenerationUtility.methodDescriptor(type_of_current_procedure,paramTypes);

        MethodVisitor procedure_visitor = cw.visitMethod(ACC_PUBLIC | ACC_STATIC, procedure_name, descriptor, null, null);
        procedure_visitor.visitCode();
        //Add the parameters to the curr_scope
        int orginal_stack_index = stack_index;
        stack_index = 0;
        for (VariableDeclaration parameter: parameters) {
            org.objectweb.asm.Type type = org.objectweb.asm.Type.getType(getFieldType(parameter.getType()));
            procedure_visitor.visitVarInsn(type.getOpcode(ILOAD), stack_index);
            procedure_scopes_tables.add(parameter.getNameOfTheVariable(),stack_index,parameter.getType());
            stack_index += type.getSize();
        }
        //Visit the block
        added_return = false;
        block.accept(this,procedure_scopes_tables,procedure_visitor);
        //if return type is void and there is no return statement, add a return statement
        if(type_of_current_procedure.getNameofTheType().equals("void") && !added_return){
            procedure_visitor.visitInsn(RETURN);
        }
        procedure_visitor.visitInsn(RETURN);

        procedure_visitor.visitMaxs(-1,-1);
        procedure_visitor.visitEnd();
        stack_index = orginal_stack_index;
        type_of_current_procedure=null;
    }

    public void visit(Block block, ScopesTable curr_scope, MethodVisitor mw){
        ArrayList<ASTNode> statements=block.getStatements();
        for (int i = 0; i < block.getStatements().size(); i++) {
           statements.get(i).accept(this, curr_scope, mw);
        }
    }

    public void visit(IfStatement ifStatement, ScopesTable curr_scope, MethodVisitor mw){
        ExpressionStatement condition = ifStatement.getIfCondition();
        Block then_block = ifStatement.getIfBlock();
        condition.accept(evaluator,curr_scope,mw);
        //If the condition is true, go to the then block
        //if the condition is false, skip block
        Label if_start = new Label();
        Label if_end = new Label();
        mw.visitJumpInsn(IFEQ, if_end);
        mw.visitLabel(if_start);
        boolean added_return_before = added_return;
        then_block.accept(this,curr_scope,mw);
        if(!added_return_before && added_return){
            added_return = false;
            //se in un if c'è un return, è come
        }
        mw.visitLabel(if_end);
    }

    public void visit(IfElseStatement ifElseStatement,ScopesTable curr_scope,MethodVisitor mw){
        ExpressionStatement condition = ifElseStatement.getIfCondition();
        Block then_block = ifElseStatement.getIfBlock();
        Block else_block = ifElseStatement.getElse_block();
        condition.accept(evaluator,curr_scope,mw);
        Label if_start = new Label();
        Label else_start = new Label();
        Label else_end = new Label();
        mw.visitJumpInsn(IFEQ, else_start);
        mw.visitLabel(if_start);
        boolean added_return_before = added_return;
        then_block.accept(this,curr_scope,mw);
        if(!added_return_before && added_return){
            added_return = false;
        }
        mw.visitJumpInsn(GOTO, else_end);
        mw.visitLabel(else_start);
        else_block.accept(this,curr_scope,mw);
        mw.visitLabel(else_end);

    }

    boolean isInsideLoop = false;
    public void visit(WhileStatement whileStatement,ScopesTable scopesTable,MethodVisitor mw){
        ExpressionStatement condition = whileStatement.getExitCondition();
        Block block = whileStatement.getBlock();
        Label while_start = new Label();
        Label while_end = new Label();
        mw.visitLabel(while_start);
        condition.accept(evaluator,scopesTable,mw);
        mw.visitJumpInsn(IFEQ, while_end);
        //useful for the understed that even i added a return,
        // i need to add a return also outside the loop
        //ex: def void procedure(){while(true){return;} return;}
        boolean iChanged=false;
        if(!isInsideLoop){
            isInsideLoop = true;
            iChanged = true;
        }
        block.accept(this,scopesTable,mw);
        if(iChanged){
            isInsideLoop = false;
        }
        mw.visitJumpInsn(GOTO, while_start);
        mw.visitLabel(while_end);
    }

    public void visit(ForStatement forStatement,ScopesTable scopesTable,MethodVisitor mw){
        VariableAssigment start_ass = forStatement.getStart();
        ExpressionStatement exit_condition = forStatement.getEndCondition();
        VariableAssigment update = forStatement.getUpdate();
        Block block = forStatement.getBlock();


        //The exit condition maybe is null, so if is null, we need to create
        //a new ExpressionStatement with value true
        exit_condition = exit_condition == null ? new Value(new Symbol(Token.BooleanValue, "true")) : exit_condition;
        //variableInstantiation.accept(this,scopesTable,mw);
        if (start_ass != null) {
            start_ass.accept(this,scopesTable,mw);
        }
        Label for_start = new Label();
        Label for_end = new Label();
        mw.visitLabel(for_start);
        exit_condition.accept(evaluator,scopesTable,mw);
        mw.visitJumpInsn(IFEQ, for_end);
        boolean iChanged=false;
        if(!isInsideLoop){
            isInsideLoop = true;
            iChanged = true;
        }
        block.accept(this,scopesTable,mw);
        if(iChanged){
            isInsideLoop = false;
        }
        if(update!=null ){
            update.accept(this,scopesTable,mw);
        }
        mw.visitJumpInsn(GOTO, for_start);
        mw.visitLabel(for_end);
    }

    public void visit(VariableInstantiation variableInstantiation, ScopesTable curr_scope,MethodVisitor mw){
        //Get name of the variable
        String var_name = variableInstantiation.getNameOfTheVariable();
        //Get type of the variable
        Type type = variableInstantiation.getType();


        //Evaluate right_side
        ExpressionStatement right_side = variableInstantiation.getRight_side();
        right_side.accept(evaluator,curr_scope,mw);
        org.objectweb.asm.Type type_op = org.objectweb.asm.Type.getType(CodeGenerationUtility.getFieldType(type));
        mw.visitVarInsn(type_op.getOpcode(ISTORE),stack_index);
        curr_scope.add(var_name,stack_index,type);
        stack_index += type_op.getSize();

    }

    public void visit(VariableDeclaration variableDeclaration, ScopesTable curr_scope, MethodVisitor mw){
        //Get name of the variable
        String var_name = variableDeclaration.getNameOfTheVariable();
        //Get type of the variable
        Type type = variableDeclaration.getType();

        //Evaluate right_side
        org.objectweb.asm.Type type_op = org.objectweb.asm.Type.getType(CodeGenerationUtility.getFieldType(type));
        curr_scope.add(var_name,stack_index,type);
        stack_index+=type_op.getSize();
    }

    public void visit(VariableAssigment variableAssigment,ScopesTable curr_scope, MethodVisitor mw){
        //Evaluate right_side
        variableAssigment.getRight_side().accept(evaluator,curr_scope,mw);
        //Assign the value to the variable
        variableAssigment.getVariable().accept(this,curr_scope,mw);
    }

    public void visit(VariableReference variableReference,ScopesTable curr_scope,MethodVisitor mw){
        //We are here because we come from a visit of a VariableAssigment
        //like a = 5;
        //now we need to store the value in the variable
        String var_name = variableReference.getIdentifier();
        Type type = curr_scope.getType(var_name);
        //Recupera l'indice della variabile
        int index = curr_scope.getIndex(var_name);
        CodeGenerationUtility.storeValueInTheVariable(var_name,index, type, mw, program_name);
    }



    public void visit(ReturnStatement returnStatement, ScopesTable currScope, MethodVisitor mw) {
        ExpressionStatement expressionStatement = returnStatement.getExpression();
        if(expressionStatement==null){
            mw.visitInsn(Opcodes.RETURN);
            if(!isInsideLoop){
                added_return = true;
            }
            return;
        }
        expressionStatement.accept(evaluator,currScope,mw);
        org.objectweb.asm.Type type = org.objectweb.asm.Type.getType(CodeGenerationUtility.getFieldType(type_of_current_procedure));
        mw.visitInsn(type.getOpcode(IRETURN));

    }
    /*

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

     */


    public void visit(FunctionCall functionCall, ScopesTable curr_scope, MethodVisitor mw) {
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
            String descriptor = constructorDescriptor(paramType);
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
