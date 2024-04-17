package compiler.Parser.AST.ASTNodes;

import compiler.Exceptions.SemanticException.SemanticErrorException;
import compiler.Lexer.Symbol;
import compiler.Parser.AST.ASTNode;
import compiler.Parser.AST.ASTNodes.Expressions.Type;
import compiler.SemanticAnalysis.SymbolTable;
import compiler.SemanticAnalysis.Visitor.Visitor;

public class VariableAssigment extends ASTNode {

    protected ExpressionStatement variable;
    protected ExpressionStatement right_side;

    public VariableAssigment(ExpressionStatement variable, ExpressionStatement right_side) {
        this.variable = variable;
        this.right_side = right_side;
    }

    public String toString() {
        return "VariableAssigment: {" +
                "Identifier: " + variable + "," +
                "Right side: " + right_side +
                "}";
    }

    public ExpressionStatement getVariable(){return this.variable;}

    public ExpressionStatement getRight_side() {
        return right_side;
    }

    @Override
    public void accept(Visitor visitor, SymbolTable symbolTable,SymbolTable structTable) throws SemanticErrorException {
        visitor.visit(this,symbolTable,structTable);
    }
}