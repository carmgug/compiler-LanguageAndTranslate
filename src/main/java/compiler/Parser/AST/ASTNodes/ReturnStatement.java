package compiler.Parser.AST.ASTNodes;

import compiler.Exceptions.SemanticException.SemanticException;
import compiler.Parser.AST.ASTNode;
import compiler.Parser.AST.ASTNodes.Expressions.Type;
import compiler.SemanticAnalysis.SymbolTable.SymbolTable;
import compiler.SemanticAnalysis.Visitor.Visitor;
import compiler.SemanticAnalysis.Visitor.VisitorType;

public class ReturnStatement extends ASTNode {

    private final ExpressionStatement expression;
    private final int line;

    public ReturnStatement(int line){//return ;
        this.expression=null;
        this.line=line;
    }
    public ReturnStatement(ExpressionStatement expression,int line){//return expression;
        this.expression=expression;
        this.line=line;
    }

    @Override
    public void accept(Visitor visitor, SymbolTable symbolTable,SymbolTable structTable) throws SemanticException {
        visitor.visit(this,symbolTable,structTable);
    }

    @Override
    public Type accept(VisitorType visitor, SymbolTable symbolTable, SymbolTable structTable) throws SemanticException {
        throw new SemanticException("Sould not run");
    }

    public ExpressionStatement gerExpression(){
        return expression;
    }



    public String toString(){
        if(expression==null){
            return "Return : void";
        }
        return "Return : { return_value_of: "+expression+"}";
    }

    public ExpressionStatement getExpression(){return  this.expression;}

    public int getLine() {
        return line;
    }
}
