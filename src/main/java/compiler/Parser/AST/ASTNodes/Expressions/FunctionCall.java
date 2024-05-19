package compiler.Parser.AST.ASTNodes.Expressions;

import compiler.CodeGenerator.CodeGenerationVisitor;
import compiler.CodeGenerator.EvaluateVisitor;
import compiler.CodeGenerator.ScopesTable;
import compiler.Exceptions.SemanticException.SemanticException;
import compiler.Lexer.Symbol;
import compiler.Lexer.Token;
import compiler.Parser.AST.ASTNodes.ExpressionStatement;
import compiler.SemanticAnalysis.SymbolTable.SymbolTable;
import compiler.SemanticAnalysis.Visitor.Visitor;
import compiler.SemanticAnalysis.Visitor.VisitorType;
import org.objectweb.asm.MethodVisitor;

import java.util.ArrayList;

public class FunctionCall extends ExpressionStatement {


    private final Symbol functionName;
    private final ArrayList<ExpressionStatement> arguments;
    private final int line;

    //So after the semantic analysis we can know the type of the function and the type of the parameters
    //This is used to generate the code, to know which function to call and which parameters to pass
    private Type returnType;
    private ArrayList<Type> parametersType;
    private boolean isConstructor;

    public void setReturnType(Type returnType){
        this.returnType=returnType;
    }

    public Type getReturnType(){
        return this.returnType;
    }

    public void setParametersType(ArrayList<Type> parametersType){
        this.parametersType=parametersType;
    }

    public ArrayList<Type> getParametersType(){
        return this.parametersType;
    }

    public void setIsConstructor(boolean isConstructor){
        this.isConstructor=isConstructor;
    }

    public boolean isConstructor(){
        return this.isConstructor;
    }



    public FunctionCall(Symbol functionName,ArrayList<ExpressionStatement> arguments) {
        if(!functionName.getType().equals(Token.Identifier)) throw new IllegalArgumentException("The function name must be an identifier");
        this.functionName = functionName;
        this.arguments=new ArrayList<>();
        for(ExpressionStatement e:arguments){
            this.arguments.add(e);
        }
        this.line=functionName.getLine();
    }


    public String getFunctionName(){
        return this.functionName.getValue();
    }

    public Symbol getFunctionSymbol(){
        return this.functionName;
    }



    public ArrayList<ExpressionStatement> getParameters() {
        return arguments;
    }


    public String toString(){
        return "FunctionCall : {" +
                    "functionName :" + functionName.getValue() + ","+
                    "arguments : " + (arguments.size()==0 ? "None" : arguments) +
                "}";
    }

    @Override
    public void accept(Visitor visitor, SymbolTable symbolTable,SymbolTable structTable) throws SemanticException {
        visitor.visit(this,symbolTable,structTable);
    }

    @Override
    public Type accept(VisitorType visitorType, SymbolTable symbolTable, SymbolTable structTable) throws SemanticException {
        return visitorType.visit(this, symbolTable,structTable);
    }

    @Override
    public void accept(CodeGenerationVisitor codeGenerationVisitor, ScopesTable curr_scope, MethodVisitor mw) {
        codeGenerationVisitor.visit(this,curr_scope,mw);
    }

    @Override
    public void accept(EvaluateVisitor visitor, ScopesTable curr_scope, MethodVisitor mw) {
        visitor.visit(this,curr_scope,mw);
    }

    public int getLine() {
        return line;
    }
}
