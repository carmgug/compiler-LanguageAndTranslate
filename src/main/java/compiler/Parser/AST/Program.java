package compiler.Parser.AST;

import Utility.Utility;
import compiler.Parser.AST.ASTNodes.Constant;
import compiler.Parser.AST.ASTNodes.GlobalVariable;
import compiler.Parser.AST.ASTNodes.Procedure;
import compiler.Parser.AST.ASTNodes.Struct;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Program implements AbstractSyntaxTree, Iterable<ASTNode>{


    ASTNode root;
    ASTNode last;

    public Program(){

    }

    public Program(ASTNode root){
        this.root = root;
        this.root.setNext(null);
        this.last=root;
    }

    public void add(ASTNode node){
        if(root==null){
            root=node;
            last=node;
            return;
        }
        if(root==last){
            root.setNext(node);
            last=node;
        }
        else{
            last.setNext(node);
            last=node;
        }
    }


    public String toString(){
        Iterator<ASTNode> it = iterator();
        StringBuilder sb=new StringBuilder();
        sb.append("Program : {");
        while(it.hasNext()){
            sb.append(it.next().toString());
            if(it.hasNext()) sb.append(",");
            else sb.append("\n");
        }
        sb.append("}");
        return Utility.indentedString(sb.toString());
    }





    @Override
    public Iterator<ASTNode> iterator() {
        return new Iterator<ASTNode>() {
            private ASTNode current = root;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public ASTNode next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                ASTNode node = current;
                current = current.getNext();
                return node;
            }
        };
    }
}
