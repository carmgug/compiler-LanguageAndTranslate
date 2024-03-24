package compiler.Parser.AST.ASTNodes.Expressions;

import compiler.Lexer.Symbol;
import compiler.Lexer.Token;
import compiler.Parser.AST.ASTNodes.ExpressionStatement;

import java.util.ArrayList;

public class FunctionCall extends ExpressionStatement {


    private Symbol functionName;
    private ArrayList<ExpressionStatement> arguments;


    public FunctionCall(Symbol functionName,ArrayList<ExpressionStatement> arguments) {
        if(!functionName.getType().equals(Token.Identifier)) throw new IllegalArgumentException("The function name must be an identifier");
        this.functionName = functionName;
        this.arguments=new ArrayList<>();
        for(ExpressionStatement e:arguments){
            this.arguments.add(e);
        }
    }
    /*
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(functionName.getValue());
        sb.append("(");
        for(ExpressionStatement e:arguments){
            sb.append(e.toString());
            sb.append(",");
        }
        if(arguments.size()>0) sb.deleteCharAt(sb.length()-1);
        sb.append(")");
        return "FunctionCall {"+sb+"}";
    }
     */

    public String toString(){
        return "FunctionCall : {" +
                    "functionName :" + functionName.getValue() + ","+
                    "arguments : " + (arguments.size()==0 ? "None" : arguments) +
                "}";
    }
}
