package compiler.SemanticAnalysis.SemanticTypes;

import compiler.SemanticAnalysis.SemanticType;

import java.util.LinkedHashMap;
import java.util.Map;

public class SemanticStructType extends SemanticType{


    private Map<String, SemanticType> fields;


    public SemanticStructType(String type, Map<String,SemanticType> fields) {
        super(type);
        this.fields=fields;
    }

    public SemanticStructType(String type) {
        super(type);
        this.fields=new LinkedHashMap<>();
    }

    public Map<String,SemanticType> getFields(){return this.fields;}

    public void addField(String variable_name,SemanticType type){
        this.fields.put(variable_name,type);
    }


}
