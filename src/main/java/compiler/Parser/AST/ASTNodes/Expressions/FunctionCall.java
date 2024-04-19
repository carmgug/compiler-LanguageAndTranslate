package compiler.Parser.AST.ASTNodes.Expressions;

import compiler.Exceptions.SemanticException.SemanticErrorException;
import compiler.Lexer.Symbol;
import compiler.Lexer.Token;
import compiler.Parser.AST.ASTNodes.ExpressionStatement;
import compiler.SemanticAnalysis.SymbolTable.SymbolTable;
import compiler.SemanticAnalysis.Visitor.Visitor;
import compiler.SemanticAnalysis.Visitor.VisitorType;

import java.util.ArrayList;

public class FunctionCall extends ExpressionStatement {


    private final Symbol functionName;
    private final ArrayList<ExpressionStatement> arguments;
    private final int line;


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
    public void accept(Visitor visitor, SymbolTable symbolTable,SymbolTable structTable) throws SemanticErrorException {
        visitor.visit(this,symbolTable,structTable);
    }

    @Override
    public Type accept(VisitorType visitorType, SymbolTable symbolTable, SymbolTable structTable) throws SemanticErrorException {
        return visitorType.visit(this, symbolTable,structTable);
    }

    public int getLine() {
        return line;
    }
}
