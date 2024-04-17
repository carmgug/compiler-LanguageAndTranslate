package compiler.SemanticAnalysis.SemanticTypes;

import compiler.SemanticAnalysis.SemanticType;

import java.util.LinkedHashMap;
import java.util.Map;

public class SemanticProcedureType {

    private Map<String, SemanticType> parameters;
    private SemanticType return_type;

    public SemanticProcedureType(Map<String,SemanticType> fields,SemanticType return_type) {
        this.parameters=fields;
        this.return_type=return_type;
    }

    public SemanticProcedureType() {
        this.parameters=new LinkedHashMap<>();
    }

    public Map<String,SemanticType> getFields(){return this.parameters;}

    public void addParameters(String variable_name,SemanticType type){
        this.parameters.put(variable_name,type);
    }

    public boolean contains(String parameter_identifier){
        if(parameters.containsKey(parameter_identifier)) return true;
        return false;
    }

}
