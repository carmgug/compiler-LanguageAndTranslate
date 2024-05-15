package compiler.Parser.AST.ASTNodes;

import compiler.CodeGenerator.CodeGenerationVisitor;
import compiler.CodeGenerator.ScopesTable;
import compiler.Exceptions.SemanticException.SemanticException;
import compiler.Parser.AST.ASTNodes.Expressions.Type;
import compiler.SemanticAnalysis.SymbolTable.SymbolTable;
import compiler.SemanticAnalysis.Visitor.Visitor;
import compiler.SemanticAnalysis.Visitor.VisitorType;
import org.objectweb.asm.MethodVisitor;

public class IfElseStatement extends IfStatement {

    private final Block else_block;
    private final int else_line;


    public IfElseStatement(ExpressionStatement if_condition,Block if_block,Block else_block,int if_line,int else_line){
        super(if_condition,if_block,if_line);
        this.else_block=else_block;
        this.else_line=else_line;
    }

    public String toString(){
        return "IfElseStatement : {" +
                "ifCondition:" + ifCondition +
                ", ifBlock : {" + ifBlock + "}"+
                ", elseBlock : {" + else_block + "}"+
                '}';
    }

    public Block getElse_block() {
        return else_block;
    }

    public int getElse_line() {
        return else_line;
    }


    @Override
    public void accept(Visitor visitor, SymbolTable symbolTable, SymbolTable structTable) throws SemanticException {
        super.accept(visitor, symbolTable, structTable);
    }

    @Override
    public Type accept(VisitorType visitor, SymbolTable symbolTable, SymbolTable structTable) throws SemanticException {
        throw new SemanticException("Sould not run");
    }

    public void accept(CodeGenerationVisitor visitor, ScopesTable scopesTable, MethodVisitor mw)  {
        visitor.visit(this, scopesTable, mw);
    }
}
