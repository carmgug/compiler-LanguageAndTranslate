package compiler.CodeGenerator;

import compiler.Parser.AST.ASTNodes.Expressions.Type;
import compiler.SemanticAnalysis.SymbolTable.SymbolTable;
import compiler.SemanticAnalysis.SymbolTable.SymbolTableEntry;

import java.util.Map;

public class ScopesTable {


    class ScopeEntry {
        Integer index;
        Type type;
        ScopeEntry(int index, Type type){
            this.index=index;
            this.type=type;
        }
    }


    protected ScopesTable previousScope; //link to previous table

    //This map will store the index of the variable in the stack
    protected Map<String, ScopeEntry> curr_scope;

    public ScopesTable(){
        this.previousScope=null;
        this.curr_scope=new java.util.LinkedHashMap<>();
    }
    public ScopesTable(ScopesTable previousScope){
        this.previousScope=previousScope;
        this.curr_scope=new java.util.LinkedHashMap<>();
    }

    public void add(String s, Integer index, Type type){
        this.curr_scope.put(s,new ScopeEntry(index,type));
    }

    public Integer getIndex(String s){
        ScopeEntry entry=curr_scope.get(s);
        if(entry==null){
            if(this.previousScope!=null){
                return previousScope.getIndex(s);
            }
            else{
                return null;
            }
        }
        return entry.index;
    }

    public Type getType(String s){
        ScopeEntry entry=curr_scope.get(s);
        if(entry==null){
            if(this.previousScope!=null){
                return previousScope.getType(s);
            }
            else{
                return null;
            }
        }
        return entry.type;
    }

}
