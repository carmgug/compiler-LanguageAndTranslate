package compiler.SemanticAnalysis.SymbolTable;

import compiler.Parser.AST.ASTNodes.Expressions.Type;

import java.util.*;

public class SymbolTable{

    protected SymbolTable previousTable; //link to previous table
    protected Map<String, SymbolTableEntry> entries;

    public SymbolTable(SymbolTable previousTable){
        this.previousTable=previousTable;
        this.entries=new LinkedHashMap<>();
    }

    public SymbolTable(){
        this.previousTable=null;
        this.entries=new LinkedHashMap<>();
    }

    public SymbolTable(Map<String, SymbolTableEntry> entries){
        this.previousTable=null;
        this.entries=entries;
    }

    public SymbolTable(SymbolTable previousTable, Map<String, SymbolTableEntry> entries){
        this.previousTable=previousTable;
        this.entries=entries;
    }

    public void add(String s, SymbolTableEntry type){
        this.entries.put(s,type);
    }

    public SymbolTableEntry get(String s){
        if(this.entries.get(s)==null){
            if(this.previousTable!=null){
                return previousTable.get(s);
            }
            else{
                return null;
            }
        }
        return this.entries.get(s);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        for (Map.Entry<String, SymbolTableEntry> entry : entries.entrySet()) {
            sb.append("\t").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        sb.append("}");
        return sb.toString();
    }
    public SymbolTable getPreviousTable() {
        return previousTable;
    }

    public Map<String, SymbolTableEntry> getEntries() {
        return entries;
    }
}
