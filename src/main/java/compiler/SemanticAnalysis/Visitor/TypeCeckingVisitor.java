package compiler.SemanticAnalysis.Visitor;

import compiler.Exceptions.SemanticException.SemanticErrorException;
import compiler.Lexer.Symbol;
import compiler.Lexer.Token;
import compiler.Parser.AST.ASTNodes.*;
import compiler.Parser.AST.ASTNodes.Expressions.*;
import compiler.Parser.AST.ASTNodes.Expressions.NegationNodes.ArithmeticNegationNode;
import compiler.Parser.AST.ASTNodes.Expressions.NegationNodes.BooleanNegationNode;
import compiler.Parser.AST.ASTNodes.Expressions.Types.ArrayType;
import compiler.Parser.AST.ASTNodes.Expressions.Types.BaseType;
import compiler.Parser.AST.ASTNodes.Expressions.Types.StructType;
import compiler.SemanticAnalysis.SymbolTable.SymbolTableEntry;
import compiler.SemanticAnalysis.SymbolTable.SymbolTable;
import compiler.SemanticAnalysis.SymbolTable.SymbolTableValues.SemanticStructType;
import compiler.SemanticAnalysis.SymbolTable.SymbolTableValues.SymbolTableProcedureType;
import compiler.SemanticAnalysis.SymbolTable.SymbolTableValues.SymbolTableProceduresEntry;
import compiler.SemanticAnalysis.SymbolTable.SymbolTableValues.SymbolTableType;

import java.util.ArrayList;

public class TypeCeckingVisitor implements VisitorType{

    @Override
    public Type visit(Constant constant, SymbolTable symbolTable, SymbolTable structTable) throws SemanticErrorException {
        return null;
    }



    @Override
    public Type visit(Value value, SymbolTable symbolTable, SymbolTable structTable){
        switch(value.getSymbol().getType()){
            case IntNumber:
                return new BaseType(new Symbol(Token.IntType,"int",value.getSymbol().getLine()));
            case FloatNumber:
                return new BaseType(new Symbol(Token.FloatType,"float",value.getSymbol().getLine()));
            case BooleanValue:
                return new BaseType(new Symbol(Token.BoolType,"bool",value.getSymbol().getLine()));
            default:
                return new BaseType(new Symbol(Token.StringType,"string",value.getSymbol().getLine()));
        }
    }

    @Override
    public Type visit(VariableReference variable, SymbolTable symbolTable, SymbolTable structTable) throws SemanticErrorException {
        if(symbolTable.get(variable.getIdentifier())==null){
            throw new SemanticErrorException("Variable '"+variable.getIdentifier()+"' not defined at line "+variable.getLine());
        }
        SymbolTableEntry type= symbolTable.get(variable.getIdentifier());
        return ((SymbolTableType) type).getType();
    }
    @Override
    public Type visit(ArithmeticNegationNode arithmeticNegationNode, SymbolTable symbolTable, SymbolTable structTable) throws SemanticErrorException {
        Type observed_Type=visit(arithmeticNegationNode.getExpression(),symbolTable,structTable);
        //Expected Type is a bool
        if(!observed_Type.equals(new BaseType(new Symbol(Token.IntType,"int"))) &&
                !observed_Type.equals(new BaseType(new Symbol(Token.FloatType,"float")))){
            throw new SemanticErrorException("Expected type Int or Float, found "+observed_Type.getSymbol().getType());
        }
        return observed_Type;
    }
    @Override
    public Type visit(BooleanNegationNode booleanNegationNode, SymbolTable symbolTable, SymbolTable structTable) throws SemanticErrorException {
        Type observed_Type=visit(booleanNegationNode.getExpression(),symbolTable,structTable);
        //Expected Type is a bool
        if(!observed_Type.equals(new BaseType(new Symbol(Token.BoolType,"bool")))){
            throw new SemanticErrorException("Expected type bool, found "+observed_Type.getSymbol().getType());
        }
        return observed_Type;
    }
    @Override
    public Type visit(StructAccess structAccess, SymbolTable symbolTable, SymbolTable structTable) throws SemanticErrorException {
        Type left_part=visit(structAccess.getLeftPart(),symbolTable,structTable);
        if(!(left_part instanceof StructType)){
            throw new SemanticErrorException("Expected a struct type, found "+left_part.getSymbol().getType());
        }
        SymbolTable struct_symbol_table=((SemanticStructType)structTable.get(left_part.getNameofTheType())).getFields();
        Type right_part=visit(structAccess.getRightPart(),struct_symbol_table ,structTable);
        return right_part;
    }

    @Override
    public Type visit(ArrayAccess arrayAccess, SymbolTable symbolTable, SymbolTable structTable) throws SemanticErrorException {
        Type left_part=visit(arrayAccess.getArray(),symbolTable,structTable);
        if(!(left_part instanceof ArrayType)){
            throw new SemanticErrorException("Expected a ArrayType, found "+left_part.getSymbol().getType());
        }
        Type index=visit(arrayAccess.getIndex(),symbolTable,structTable);
        //the index must be an int
        if(!index.getSymbol().getType().equals(Token.IntType)){
            throw new SemanticErrorException("Expected a int, found "+index.getSymbol().getType());
        }
        return  left_part;
    }

    @Override
    public Type visit(ExpressionStatement expressionStatement, SymbolTable symbolTable, SymbolTable structTable) throws SemanticErrorException {
        return expressionStatement.accept(this,symbolTable,structTable);
    }

    @Override
    public Type visit(FunctionCall functionCall,SymbolTable symbolTable,SymbolTable structTable) throws SemanticErrorException {
        //We are performing a semanticAnalysis on a functionCall
        //The functionCall is already in the symboltTable
        //We need to check if the function is already defined
        //The function call could be a procedure or a new type of a existing struct
        //We need to check if the function is already defined
        String functionName= functionCall.getFunctionName();
        SymbolTableEntry functionEntry=symbolTable.get(functionName);
        SymbolTableEntry structEntry=structTable.get(functionName);
        if(functionEntry==null && structEntry==null){
            //if the functionName is not in the symbolTable and in the structTable we throw an exception
            //We need to check the structTable beacuse it could be a constructorCall of a new type of a existing struct
            throw new SemanticErrorException("Function call '"+functionName +"' is not defined"+ "(line "+functionCall.getFunctionSymbol().getLine()+")");
        }
        //We need to check if the arguments are the same type as the parameters of the function
        //We need to check if the function is a procedure or a constructorCall
        if(functionEntry!=null) {
            //We are calling a procedure, we need to check if the arguments are the same type as the parameters of the function
            //Ok we take the all procedure with the same name of functionCall
            SymbolTableProceduresEntry proceduresEntry = (SymbolTableProceduresEntry) functionEntry;
            //Ok we take the parameters of fucntionCall
            ArrayList<ExpressionStatement> parameters = functionCall.getParameters();
            //We need to check if there is a procedure with the same parameters
            for(SymbolTableProcedureType procedure:proceduresEntry.getProcedures()){
                if(procedure.getFields().size()==parameters.size()){
                    boolean sameParameters=true;
                    int idx=0;
                    for ( String curr_parameter : procedure.getFields().keySet()) {
                        Type expected_type=((SymbolTableType) procedure.getFields().get(curr_parameter)).getType();
                        Type observed_type=this.visit(parameters.get(idx),symbolTable,structTable);
                        if(!expected_type.equals(observed_type)){
                            sameParameters=false;
                            break;
                        }
                        idx++;
                    }
                    if(sameParameters){ //ho gli stessi parametri
                        return procedure.getReturnType().getType();
                    }
                }
            }
            //Se arrivi qui problema
            throw new SemanticErrorException("Function call '"+functionName +"' has not the same parameters as the function definition"+ "(line "+functionCall.getFunctionSymbol().getLine()+")");
        }
        else{
            //we are calling a constructorCall
            //We need to check if the arguments are the same type as the parameters of the function
            SemanticStructType structType=(SemanticStructType) structEntry;
            //Ok we take the parameters of fucntionCall
            ArrayList<ExpressionStatement> parameters = functionCall.getParameters();
            int idx=0;
            for(String key:structType.getFields().getEntries().keySet()){
                Type expected_type = ((SymbolTableType) structType.getFields().get(key)).getType();
                Type observed_type = this.visit(parameters.get(idx), symbolTable, structTable);
                if(!expected_type.equals(observed_type)) {
                    throw new SemanticErrorException("Constructor call '" + functionName + "' has not the same parameters as the struct definition" + "(line " + functionCall.getFunctionSymbol().getLine() + ")"+
                            " Expected: "+expected_type+" Observed: "+observed_type);
                }
                idx++;

            }
            return structType.getType();
        }
    }
    @Override
    public Type visit(BinaryExpression binaryExpression, SymbolTable symbolTable, SymbolTable structTable) throws SemanticErrorException {
        Type leftType= visit(binaryExpression.getLeft(),symbolTable,structTable);
        Type rightType= visit(binaryExpression.getRight(),symbolTable,structTable);
        if(!leftType.equals(rightType)){

            boolean case_left_int_right_float=leftType.getSymbol().getType().equals(Token.IntType) && rightType.getSymbol().getType().equals(Token.FloatType);
            boolean case_left_float_right_int=leftType.getSymbol().getType().equals(Token.FloatType) && rightType.getSymbol().getType().equals(Token.IntType);
            if(!case_left_float_right_int && !case_left_int_right_float){
                throw new SemanticErrorException("Type mismatch in BinaryExpression: "+binaryExpression);
            }
        }
        Operator op=binaryExpression.getOperator();
        checkOperator(op,leftType,rightType); //ok the operator can be applied to the two types
        //I need to return the type of the operation (int,float,bool,string)
        return typeResultOperator(op,leftType,rightType);
    }

    @Override
    public Type visit(Operator operator, Type leftType, Type rightType) throws SemanticErrorException {
        return null;
    }

    @Override
    public Type visit(ArrayValueDeclaration arrayValueDeclaration, SymbolTable symbolTable, SymbolTable structTable) throws SemanticErrorException {
        Type initialType=null;
        for(ExpressionStatement exp: arrayValueDeclaration.getValues()){
            Type observedType=this.visit(exp,symbolTable,structTable);
            if(initialType==null){
                initialType=observedType;
            }else{
                if(!initialType.equals(observedType)){
                    throw new SemanticErrorException("The array contains different Type ("+initialType+", "+observedType+")");
                }
            }
        }
        //The array is empty
        return initialType;
    }

    private BaseType typeResultOperator(Operator op, Type leftType, Type rightType){
        switch (op.getSymbol().getType()){
            case AdditiveOperator:
            case MultiplicativeOperator:
                if(leftType.getSymbol().getType().equals(Token.IntType) && rightType.getSymbol().getType().equals(Token.IntType)){
                    return new BaseType(new Symbol(Token.IntType,"int"));
                }
                else if(leftType.getSymbol().getType().equals(Token.FloatType) && rightType.getSymbol().getType().equals(Token.FloatType)){
                    return new BaseType(new Symbol(Token.FloatType,"float"));
                }
                else if(leftType.getSymbol().getType().equals(Token.IntType) && rightType.getSymbol().getType().equals(Token.FloatType)){
                    return new BaseType(new Symbol(Token.FloatType,"float"));
                }
                else if(leftType.getSymbol().getType().equals(Token.FloatType) && rightType.getSymbol().getType().equals(Token.IntType)){
                    return new BaseType(new Symbol(Token.FloatType,"float"));
                }
                break;
            case ComparisonOperator :
                return new BaseType(new Symbol(Token.BoolType,"bool"));
            case LogicalOperator:
                return new BaseType(new Symbol(Token.BoolType,"bool"));
        }
        //Never Reached

        return null;
    }

    private void checkOperator(Operator operator,Type leftType,Type rightType) throws SemanticErrorException {

        Token[] allowed_types=allowedTypes(operator);
        boolean allowed=false;
        for(Token allowed_type:allowed_types){
            if(leftType.getSymbol().getType().equals(allowed_type)){
                allowed=true;
                break;
            }
        }
        if(!allowed){
            throw new SemanticErrorException("Operator: " + operator + " doesn't allow type (" + leftType + ")");
        }

    }


    private Token[] allowedTypes(Operator operator){
        Token op=operator.getSymbol().getType();
        if(op.equals(Token.AdditiveOperator) || op.equals(Token.MultiplicativeOperator)){
            return new Token[]{Token.IntType,Token.FloatType};
        }
        else if(op.equals(Token.ComparisonOperator)){
            return new Token[]{Token.IntType,Token.FloatType,Token.BoolType,Token.StringType};
        }
        else if(op.equals(Token.LogicalOperator)){
            return new Token[]{Token.BoolType};
        }
        //Never Reached
        return null;
    }





}

