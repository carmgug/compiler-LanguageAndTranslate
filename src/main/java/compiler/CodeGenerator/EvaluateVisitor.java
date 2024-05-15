package compiler.CodeGenerator;

import compiler.Lexer.Symbol;
import compiler.Lexer.Token;
import compiler.Parser.AST.ASTNodes.Block;
import compiler.Parser.AST.ASTNodes.ExpressionStatement;
import compiler.Parser.AST.ASTNodes.Expressions.*;
import compiler.Parser.AST.ASTNodes.VariableDeclaration;
import compiler.Parser.AST.ASTNodes.VariableInstantiation;
import compiler.SemanticAnalysis.SymbolTable.SymbolTable;
import compiler.SemanticAnalysis.SymbolTable.SymbolTableEntry;
import compiler.SemanticAnalysis.SymbolTable.SymbolTableValues.SymbolTableProceduresEntry;
import compiler.SemanticAnalysis.SymbolTable.SymbolTableValues.SymbolTableType;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
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



    public EvaluateVisitor(CodeGenerationVisitor codeGeneratorVisitor, String program_name, SymbolTable globalTable,SymbolTable structTable){
        this.codeGeneratorVisitor = codeGeneratorVisitor;
        this.program_name = program_name;
        this.globalTable = globalTable;
    }

    public void visit(Value value, ScopesTable curr_scope, MethodVisitor mw){
        System.out.println("Visiting Value");
        //Il value può essere un intero, un booleano, una stringa o un carattere
        String v = value.getValue();

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
        System.out.println("Visiting VariableReference");

        //Recupera il nome della variabile
        String var_name = variableReference.getIdentifier();
        Type type = curr_scope.getType(var_name);
        //Recupera l'indice della variabile
        if (visiting_struct){
            //if i am visiting a struct so i need to use GETFIELD instead of ILOAD
            mw.visitFieldInsn(GETFIELD, struct_type_to_visit.getNameofTheType(), var_name, getFieldType(struct_field_type_to_visit));
            return;
        }
        int index = curr_scope.getIndex(var_name);
        //se index è -1 allora parliamo di una variabile globale/final
        //altrimenti è una variabile locale
        loadVariable(index, type, mw);
        //how to see the type of var on the stack at specified index
        //Store the value of the variable in the stack
        //mw.visitVarInsn(ASTORE,codeGeneratorVisitor.get_stack_index());

    }

    public void visit(FunctionCall functionCall, ScopesTable curr_scope, MethodVisitor mw){
        System.out.println("Visiting FunctionCall");
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
        //Ok now i have the object on the stack, now i need to access the field
        //Now access the field
        //structAccess.getRightPart().accept(this,curr_scope,mw);
        //ok now visit the right part but instead of using curr_scop you need to use GETFIELD;
        structAccess.getRightPart().accept(this,curr_scope,mw);
        visiting_struct = false;
        struct_type_to_visit = null;
        struct_field_type_to_visit = null;

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

    private void loadVariable(int index, Type type, MethodVisitor mw){
        switch (type.getTokenType()) {
            case IntType:
                if(index==-1) {
                    mw.visitFieldInsn(GETSTATIC, program_name, "I", "I");
                    return;
                }
                mw.visitVarInsn(ILOAD, index);
                return;
            case BoolType:
                if(index==-1) {
                    mw.visitFieldInsn(GETSTATIC, program_name, "Z", "Z");
                    return;
                }
                mw.visitVarInsn(ILOAD, index);
                return;
            case FloatType:
                if(index==-1) {
                    mw.visitFieldInsn(GETSTATIC, program_name, "F", "F");
                    return;
                }
                mw.visitVarInsn(FLOAD, index);
                return;
            case StringType:
                if(index==-1) {
                    mw.visitFieldInsn(GETSTATIC, program_name, "Ljava/lang/String;", "Ljava/lang/String;");
                    return;
                }
                mw.visitVarInsn(ALOAD, index);
                break;
            default:
                //Ok it is a struct so like this
                //p.field
                if(index==-1) {
                    mw.visitFieldInsn(GETSTATIC, program_name, type.getNameofTheType(), "L"+type.getNameofTheType()+";");
                    return;
                }
                mw.visitVarInsn(ALOAD, index);
        }
    }

    public void visit(BinaryExpression binaryExpression,ScopesTable curr_scope, MethodVisitor mw){
        System.out.println("Visiting BinaryExpression");
        //Visita il figlio sinistro
        binaryExpression.getLeft().accept(this,curr_scope,mw);
        //Visita il figlio destro
        binaryExpression.getRight().accept(this,curr_scope,mw);
        //Effettua l'operazione richiesta
        switch(binaryExpression.getOperator().getOperator()){
            case "+":
                mw.visitInsn(IADD);
                break;
            case "-":
                mw.visitInsn(ISUB);
                break;
            case "*":
                mw.visitInsn(IMUL);
                break;
            case "/":
                mw.visitInsn(IDIV);
                break;
            case "%":
                mw.visitInsn(IREM);
                break;
            case "==":
                //if equal return true else false
                Label startLabel = new Label();
                Label endLabel = new Label();
                //if_icmpeq compares the two values on the top of the stack and if they are equal it jumps to the label
                mw.visitJumpInsn(IF_ICMPEQ, startLabel);
                mw.visitInsn(ICONST_0);
                mw.visitJumpInsn(GOTO, endLabel);
                mw.visitLabel(startLabel);
                mw.visitInsn(ICONST_1);
                mw.visitLabel(endLabel);
                break;
            case "!=":
                //if not equal return true else false
                Label startLabel_2=new Label();
                Label endLabel_2=new Label();
                mw.visitJumpInsn(IF_ICMPNE,startLabel_2);
                mw.visitInsn(ICONST_0);
                mw.visitJumpInsn(GOTO,endLabel_2);
                mw.visitLabel(startLabel_2);
                mw.visitInsn(ICONST_1);
                mw.visitLabel(endLabel_2);
                break;
            case "<":
                //if less than return true else false
                Label startLabel_3=new Label();
                Label endLabel_3=new Label();
                mw.visitJumpInsn(IF_ICMPLT,startLabel_3);
                mw.visitInsn(ICONST_1);
                mw.visitJumpInsn(GOTO,endLabel_3);
                mw.visitLabel(startLabel_3);
                mw.visitInsn(ICONST_0);
                mw.visitLabel(endLabel_3);
                break;
            case ">":
                //if greater than return true else false
                Label startLabel_4=new Label();
                Label endLabel_4=new Label();
                mw.visitJumpInsn(IF_ICMPGT,startLabel_4);
                mw.visitInsn(ICONST_1);
                mw.visitJumpInsn(GOTO,endLabel_4);
                mw.visitLabel(startLabel_4);
                mw.visitInsn(ICONST_0);
                mw.visitLabel(endLabel_4);
                break;
            case ">=":
                //if greater than or equal return true else false
                Label startLabel_5=new Label();
                Label endLabel_5=new Label();
                mw.visitJumpInsn(IF_ICMPGE,startLabel_5);
                mw.visitInsn(ICONST_1);
                mw.visitJumpInsn(GOTO,endLabel_5);
                mw.visitLabel(startLabel_5);
                mw.visitInsn(ICONST_0);
                mw.visitLabel(endLabel_5);
                break;
            case "<=":
                //if less than or equal return true else false
                Label startLabel_6=new Label();
                Label endLabel_6=new Label();
                mw.visitJumpInsn(IF_ICMPLE,startLabel_6);
                mw.visitInsn(ICONST_1);
                mw.visitJumpInsn(GOTO,endLabel_6);
                mw.visitLabel(startLabel_6);
                mw.visitInsn(ICONST_0);
                mw.visitLabel(endLabel_6);
                break;
            case "&&":
                mw.visitInsn(IAND);
                break;
            case "||":
                mw.visitInsn(IOR);
                break;
        }
    }



}
