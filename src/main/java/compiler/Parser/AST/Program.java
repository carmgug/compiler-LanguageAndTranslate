package compiler.Parser.AST;

import compiler.Parser.AST.ASTNodes.Constant;
import compiler.Parser.AST.ASTNodes.GlobalVariable;
import compiler.Parser.AST.ASTNodes.Procedure;
import compiler.Parser.AST.ASTNodes.Struct;
import compiler.Parser.JsonFormatter.JsonFormatter;
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
        System.out.println(sb.toString());

        return indentedString(sb.toString());
    }


    public String indentedString(String jsonString){
        // Indentazione desiderata
        String indent = "      "; // 4 spazi per l'indentazione

        // Indentazione iniziale
        StringBuilder indentedString = new StringBuilder();
        int indentationLevel = 0;

        // Analizza la stringa carattere per carattere
        for (char c : jsonString.toCharArray()) {
            if (c == '{') {
                indentedString.append(c).append("\n"); // Aggiunge la graffa aperta e va a capo
                indentationLevel++; // Incrementa il livello di indentazione
                for(int i=0; i<indentationLevel; i++) indentedString.append(indent); // Aggiunge l'indentazione
            } else if (c == '}') {
                indentedString.append("\n"); // Va a capo prima della graffa chiusa
                indentationLevel--; // Decrementa il livello di indentazione
                for(int i=0; i<indentationLevel; i++) indentedString.append(indent); // Aggiunge l'indentazione
                indentedString.append(c); // Aggiunge la graffa chiusa
            }else if(c == '['){
                indentedString.append(c).append("\n"); // Aggiunge la graffa aperta e va a capo
                indentationLevel++; // Incrementa il livello di indentazione
                for(int i=0; i<indentationLevel; i++) indentedString.append(indent); // Aggiunge l'indentazione
            }else if(c==']'){
                indentedString.append("\n"); // Va a capo prima della graffa chiusa
                indentationLevel--; // Decrementa il livello di indentazione
                for(int i=0; i<indentationLevel; i++) indentedString.append(indent); // Aggiunge l'indentazione
                indentedString.append(c); // Aggiunge la graffa chiusa
            }
            else if (c == ',') {
                indentedString.append(c).append("\n"); // Aggiunge la virgola e va a capo
                for(int i=0; i<indentationLevel; i++) indentedString.append(indent); // Aggiunge l'indentazione
            } else {
                indentedString.append(c); // Aggiunge il carattere corrente
            }
        }

        // Stampa la stringa indentata
        return indentedString.toString();
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
