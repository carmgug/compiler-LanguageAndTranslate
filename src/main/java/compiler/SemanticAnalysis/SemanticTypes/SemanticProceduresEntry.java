package compiler.SemanticAnalysis.SemanticTypes;

import compiler.Exceptions.SemanticException.SemanticErrorException;
import compiler.Exceptions.SemanticException.TypeErrorException;
import compiler.Parser.AST.ASTNodes.VariableDeclaration;
import compiler.SemanticAnalysis.SemanticType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;


/*
    Class to allow different procedure with same name, same return type, but different
    list of parameters
 */
public class SemanticProceduresEntry extends SemanticType {

    private LinkedList<SemanticProcedureType> procedures;

    public SemanticProceduresEntry(String type){
        super(type);
        procedures=new LinkedList<>();
    }

    public void addFunction(ArrayList<VariableDeclaration> parameters,SemanticType returnType) throws SemanticErrorException{
        Map<String, SemanticType> map_parameters=new LinkedHashMap<>();
        for(VariableDeclaration v:parameters){
            String name_variable=v.getIdentifier().getIdentifier();
            SemanticType variable_type=new SemanticType(v.getType().getSymbol().getValue());
            map_parameters.put(name_variable,variable_type);
        }
        SemanticProcedureType curr_procedure=new SemanticProcedureType(map_parameters,returnType);
        procedures.add(curr_procedure);
    }

    public boolean containsAProcedureWithParameters(ArrayList<VariableDeclaration> parameters){
        boolean alreadyExist=false;
        for(SemanticProcedureType procedure:procedures){
            if(procedure.getFields().size()==parameters.size()){
                boolean sameParameters=true;
                int idx=0;
                for (Map.Entry<String, SemanticType> entry : procedure.getFields().entrySet()) {
                    VariableDeclaration curr=parameters.get(idx);
                    boolean sameType=entry.getValue().getType().equals(curr.getType().getSymbol().getValue());
                    if(!sameType){
                        sameParameters=false;
                        break;
                    }
                    idx++;
                }
                if(sameParameters){
                    alreadyExist=true;
                    break;
                }
            }
        }
        return alreadyExist;


    }


}
