package compiler.SemanticAnalysis.SymbolTable.SymbolTableValues;

import compiler.Exceptions.SemanticException.SemanticException;
import compiler.Parser.AST.ASTNodes.Expressions.Type;
import compiler.Parser.AST.ASTNodes.VariableDeclaration;
import compiler.SemanticAnalysis.SymbolTable.SymbolTableEntry;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;


/*
    Class to allow different procedure with same name, same return type, but different
    list of parameters
 */
public class SymbolTableProceduresEntry extends SymbolTableEntry {

    private LinkedList<SymbolTableProcedureType> procedures;

    public SymbolTableProceduresEntry(){
        procedures=new LinkedList<>();
    }

    public void addFunction(ArrayList<VariableDeclaration> parameters, SymbolTableType returnType) throws SemanticException {
        Map<String, SymbolTableEntry> map_parameters=new LinkedHashMap<>();
        for(VariableDeclaration v:parameters){
            String name_variable=v.getIdentifier().getIdentifier();
            SymbolTableEntry variable_type=new SymbolTableType(v.getType());
            map_parameters.put(name_variable,variable_type);
        }
        SymbolTableProcedureType curr_procedure=new SymbolTableProcedureType(map_parameters,returnType);
        procedures.add(curr_procedure);
    }

    public SymbolTableType getReturnTypeOfProcedureWithTypes(ArrayList<SymbolTableType> parameters) throws SemanticException {
        for(SymbolTableProcedureType procedure:procedures){
            if(procedure.getFields().size()==parameters.size()){
                boolean sameParameters=true;
                int idx=0;
                for (Map.Entry<String, SymbolTableEntry> entry : procedure.getFields().entrySet()) {
                    SymbolTableType curr=parameters.get(idx);
                    boolean sameType=((SymbolTableType)entry.getValue()).getType().equals(curr.getType());
                    if(!sameType){
                        sameParameters=false;
                        break;
                    }
                    idx++;
                }
                if(sameParameters){
                    return procedure.getReturnType();
                }
            }
        }
        //This should never happen
        throw new SemanticException("Error in getReturnTypeOfProcedureWithTypes");
    }

    public void addFunction(SymbolTableProcedureType procedure){
        procedures.add(procedure);
    }



    public boolean containsAProcedureWithParameters(ArrayList<VariableDeclaration> parameters){
        boolean alreadyExist=false;
        for(SymbolTableProcedureType procedure:procedures){
            if(procedure.getFields().size()==parameters.size()){
                boolean sameParameters=true;
                int idx=0;
                for (Map.Entry<String, SymbolTableEntry> entry : procedure.getFields().entrySet()) {
                    VariableDeclaration curr=parameters.get(idx);
                    boolean sameType=((SymbolTableType)entry.getValue()).getType().equals(curr.getType());
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

    public LinkedList<SymbolTableProcedureType> getProcedures() {
        return procedures;
    }

    public String toString(){
        StringBuilder sb=new StringBuilder();
        sb.append(" Procedure {\n");
        for(SymbolTableProcedureType procedure:procedures){
            sb.append("\t\t").append(procedure).append("\n");
        }
        sb.append("\t}");
        return sb.toString();
    }


}
