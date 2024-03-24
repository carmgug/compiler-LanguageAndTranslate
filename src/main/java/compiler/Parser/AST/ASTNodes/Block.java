package compiler.Parser.AST.ASTNodes;

import compiler.Parser.AST.ASTNode;

import java.util.ArrayList;
import java.util.Arrays;

public class Block extends ASTNode{

    private ArrayList<ASTNode> statements;

    public Block(ArrayList<ASTNode> statements){
        this.statements= new ArrayList<>(statements);

    }

    public String toString(){
        if(statements.isEmpty())
            return "Block : None";
        return "Block : "+ Arrays.toString(statements.toArray());
    }



}
