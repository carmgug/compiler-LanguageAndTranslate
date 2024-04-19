package compiler.SemanticAnalysis.SymbolTable.SymbolTableValues;

import compiler.SemanticAnalysis.SymbolTable.SymbolTableEntry;

import java.util.LinkedHashMap;
import java.util.Map;

public class SymbolTableProcedureType {

    private final Map<String, SymbolTableEntry> parameters;
    private final SymbolTableType return_type;

    public SymbolTableProcedureType(Map<String, SymbolTableEntry> fields, SymbolTableType return_type) {
        this.parameters=fields;
        this.return_type=return_type;
    }



    public SymbolTableProcedureType(SymbolTableType return_type) {
        this.parameters=new LinkedHashMap<>();
        this.return_type=return_type;
    }

    public Map<String, SymbolTableEntry> getFields(){return this.parameters;}

    public void addParameter(String variable_name, SymbolTableEntry type){
        this.parameters.put(variable_name,type);
    }

    public boolean contains(String parameter_identifier){
        if(parameters.containsKey(parameter_identifier)) return true;
        return false;
    }

    public SymbolTableType getReturnType(){
        return this.return_type;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Parameters: ");
        for (Map.Entry<String, SymbolTableEntry> entry : parameters.entrySet()) {
            sb.append(entry.getKey() + " : " + entry.getValue().toString() + ", ");
        }
        sb.append("Return type: " + return_type.toString());
        return sb.toString();
    }

}
