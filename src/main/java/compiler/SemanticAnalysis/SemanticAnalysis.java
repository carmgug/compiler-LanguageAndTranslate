package compiler.SemanticAnalysis;

import compiler.Exceptions.ParserExceptions.ParserException;
import compiler.Exceptions.SemanticException.SemanticErrorException;
import compiler.Exceptions.SemanticException.TypeErrorException;
import compiler.Lexer.Symbol;
import compiler.Lexer.Token;
import compiler.Parser.AST.ASTNode;
import compiler.Parser.AST.ASTNodes.*;
import compiler.Parser.AST.ASTNodes.Expressions.*;
import compiler.Parser.AST.ASTNodes.Expressions.NegationNodes.ArithmeticNegationNode;
import compiler.Parser.AST.ASTNodes.Expressions.NegationNodes.BooleanNegationNode;
import compiler.Parser.AST.ASTNodes.Expressions.Types.BaseType;
import compiler.Parser.AST.Program;
import compiler.Parser.Parser;
import compiler.SemanticAnalysis.SemanticTypes.SemanticRecordType;
import compiler.SemanticAnalysis.Visitor.SemanticAnlysisVisitor;
import compiler.SemanticAnalysis.Visitor.SymbolTableUpdater;

import javax.swing.plaf.synth.SynthTextAreaUI;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.*;

public class SemanticAnalysis {
    private final Parser parser;

    private final SymbolTable globalTable= new SymbolTable();

    private final SymbolTable structTable= new SymbolTable();

    private final SymbolTableUpdater symbolTableUpdater=new SymbolTableUpdater();
    private final SemanticAnlysisVisitor semanticAnalysisVisitor=new SemanticAnlysisVisitor();

    public SemanticAnalysis(Parser parser){
        this.parser=parser;
    }

    public SemanticAnalysis(Reader r,boolean debugLexer,boolean debugParser){
        this.parser= new Parser(r,debugLexer,debugParser);
    }

    public static void main(String[] args) throws IOException, ParserException, TypeErrorException, SemanticErrorException {

        String test="final bool isEmpty = isTrue(isTrue()[4.getARandomNumber().ciao[4]]);\nfinal int a_abc_12=  3;\n" +
                "final int a_abc_123_ = 3;\n" +
                "final int[] a_abc_123 = {1,2,3,4,5,6,7,8,9,10};\n" +
                "final float j = 3.256*5.0;\n" +
                "final int k = i*3;\n" +
                "final string message = \"Hello\\n\";\n" +
                "final int x=3;\nfinal int[] x=\"ciao\"+3+4+carmelo.gugliotta.x+x\n;" +
                "struct Point {\n" +
                "\tint x;\n" +
                "\tint y;\n" +
                "}\n" +
                "\n" +
                "struct Person {\n" +
                "\tstring name;\n" +
                "\tPoint location;\n" +
                "\tint[] history;}\n" +
                "\tperson age=Person(\"carmelo\",\"gugliotta\",24).age;\n" +
                "int x=3;\n"+
                "def void ciao(){for(i[3].c[4]=4,i<3,i[3].c[3]++){return a*b;} return a*b;}\n"+
                "int x=3;\n"+
                "def int[] getArrayFromString(String s){int[] ris=int[len(s)];for(i=0,i<len(s),i++){ris[i]=s[i];}return ris;x[3].ciao=3;}\n" +
                "int[] x=getArrayFromString(\"ciao\");\n";

        String test2="def void ciao(int a, int b){}";

        String test3="struct Point {\n" +
                "\tint x;\n" +
                "\tint y;\n" +
                "}\n" +
                "struct Person {\n" +
                "\tint s;\n" +
                "\tbool p;" +
                "Point p1;}\n"+
                "int i=3;"+
                "Person p1 = Person(2,true, Point(2,3));"+
                "def void metod(){ p1.p1.x=2; i=5; bool flag=true; int i; int b=4;}" +
                "def void metod(String s, int i){}" +
                "def void metod( int i,String s){}";


        String test4="bool ret=false;" +
                "def bool metod(string s, bool flag){int i=4;" +
                "bool flag2=false;" +
                "i=67;" +
                "for(i=0,i<10,i++){" +
                "int b=4;" +
                "bool flag3=flag || flag2;" +
                "}" +
                "return ret;" +
                "}";

        StringReader stringReader= new StringReader(test3);
        SemanticAnalysis s = new SemanticAnalysis(stringReader, false, false);
        //s.analize(s.parser.getAST());
        s.intilizeSymbolTable(s.parser.getAST());

    }


    public void intilizeSymbolTable(Program program) throws SemanticErrorException {
        Iterator<ASTNode> it= program.iterator(); //Si itera sull'AST creato dal Parser
        while(it.hasNext()){
            ASTNode next=it.next();
            next.accept(symbolTableUpdater,globalTable,structTable);
        }
        System.out.println(structTable);
        System.out.println(globalTable);

    }

    public void performSemanticAnalysis(Program program) throws SemanticErrorException{
        Iterator<ASTNode> it= program.iterator();
        while(it.hasNext()){
            ASTNode next=it.next();
            next.accept(symbolTableUpdater,globalTable,structTable);
        }

    }




    public void analize(Program program) throws TypeErrorException {
        Iterator<ASTNode> it= program.iterator(); //Si itera sull'AST creato dal Parser
        while(it.hasNext()){
            ASTNode next=it.next();

            while(next instanceof Constant c){ //Aggiungiamo alla HashMap tutte le costanti
                //System.out.println(c);
                addConstant(c);
                break;
            }
            while(next instanceof Struct s){
                addStruct(s);
                break;
            }
            while(next instanceof GlobalVariable gv){
                addGlobalVariable(gv);
                break;
            }
            while(next instanceof Procedure p) {
                addProcedure(p);
                break;
            }
        }
        System.out.println(globalTable);
        System.out.println(structTable);
    }


    private void addConstant(Constant c)  throws TypeErrorException{
        String id=c.getIdentifier().getValue(); //Si preleva l'identifier
        Type type=c.getType(); //Si preleva il tipo
        //Si verifica che non sia gia stata definita una variabile con stesso nome
        if(this.globalTable.get(id)!=null){
            throw new TypeErrorException("Constant "+id +" already defined "+ "(line "+c.getIdentifier().getLine()+")");
        }
        checkExpression(this.globalTable, c.getRight_side(),type.getSymbol().getValue());
        this.globalTable.add(id,new SemanticType(type.getSymbol().getValue()));
    }

    private void checkExpression(SymbolTable symbolTable, ExpressionStatement exp, String type) throws TypeErrorException {
        if(exp instanceof BinaryExpression bExp){
            checkOperatorType(bExp.getOperator(), type);
            checkExpression(symbolTable,((BinaryExpression) exp).getLeft(),type);
            checkExpression(symbolTable,((BinaryExpression) exp).getRight(),type);
        }
        if(exp instanceof ArrayAccess arrayAccess){
            if(!(arrayAccess.getArray() instanceof VariableReference variableReference)){
                throw new TypeErrorException("ArrayAccess error");
            }else{
                if(getTypeFromSymbolsTable(symbolTable,variableReference.getIdentifier())==null ){
                    throw new TypeErrorException("Array "+variableReference.getIdentifier()+" not defined");
                }
                /*
                if(!(this.globalTable.get(variableReference.getIdentifier()) instanceof SemanticArrayType)){
                    throw new TypeErrorException(variableReference.getIdentifier()+" not defined as array");
                }
                */
                checkExpression(symbolTable,arrayAccess.getIndex(), "");
            }
        }
        if(exp instanceof BooleanNegationNode boolNeg){
            if(!type.equals("bool")){
                throw new TypeErrorException("");
            }
            else{
                checkExpression(symbolTable,boolNeg.getExpression(),type);
            }
        }
        if(exp instanceof ArithmeticNegationNode arithNeg){
            if(!type.equals("int") &&
               !type.equals("float") ){
                throw new TypeErrorException("");
            }
            else{ //Si fa il check del contenuto della negazione
                checkExpression(symbolTable,arithNeg.getExpression(),type);
            }
        }
        if(exp instanceof Value v){
            if(!(v.getSymbol().getType().toString().equals(type))){
                throw new TypeErrorException("Type error exception of the variable ");
            }
        }
        if(exp instanceof ArrayInitialization arrayInstantiation){
            if(!(arrayInstantiation.getType().getSymbol().getValue().equals(type))){
                throw new TypeErrorException("Type error exception of the variable ");
            }
            //checkExpression(arrayInstantiation.getSize(), "");
        }
        if(exp instanceof ArrayValueDeclaration arrayValueDeclaration){
            ArrayList<ExpressionStatement> values= arrayValueDeclaration.getValues();
            for(ExpressionStatement value: values){
                checkExpression(symbolTable,value,type);
            }
        }
        if(exp instanceof StructAccess structAccess){
            VariableReference variableReference = (VariableReference) structAccess.getLeftPart();
            if(getTypeFromSymbolsTable(symbolTable,variableReference.getIdentifier())==null){
                throw new TypeErrorException("Type error exception of the variable "); //structNotDefined
            }else{
                //potrebbe servire un metodo specifico checkFunctionInStruct
                FunctionCall functionCall=(FunctionCall) structAccess.getRightPart();
                //verificare se la function call è contenuta nei metodi della struct
                //FunctionCall : {functionName :ciao,arguments : [Value: {Type: int,Value: 323}]}}
            }
        }
        if(exp instanceof FunctionCall functionCall){ //Supponendo non sia ammessa la presenza di struct e function con egual nome
            //Si verifica che la function è o stata definita o è una struct
            if(getTypeFromSymbolsTable(symbolTable,functionCall.getFunctionName().getValue())==null
              && this.structTable.get(functionCall.getFunctionName().getValue())==null){
                throw new TypeErrorException("Function not Defined"); //structNotDefined
            }
            if(getTypeFromSymbolsTable(symbolTable,functionCall.getFunctionName().getValue())!=null){ //è un metodo
                //Si preleva la TreeMap contenente i fields della funzione
                LinkedHashMap<String,SemanticType> fields=(LinkedHashMap) ((SemanticRecordType)this.globalTable.get(functionCall.getFunctionName().getValue())).getFields();
                checkFields(fields, functionCall.getArguments());
            }else{ // è una struct
                LinkedHashMap<String,SemanticType> fields=(LinkedHashMap) ((SemanticRecordType)this.structTable.get(functionCall.getFunctionName().getValue())).getFields();
                checkFields(fields, functionCall.getArguments());
            }
        }
        if(exp instanceof VariableReference variableReference){
            if(getTypeFromSymbolsTable(symbolTable,variableReference.getIdentifier())==null){
                throw new TypeErrorException("Variable "+variableReference.getIdentifier()+" not defined");
            } else if (!getTypeFromSymbolsTable(symbolTable,variableReference.getIdentifier()).getType().equals(type)) {
                throw new TypeErrorException("Type not matched"); //la variabile ha un tipo diverso rispetto all'instanziazione
            }
        }

    }

    private SemanticType getTypeFromSymbolsTable(SymbolTable symbolTable, String id){
        while(symbolTable!=null){
            if(symbolTable.get(id)!=null){
                return symbolTable.get(id);
            }
            symbolTable=symbolTable.getPreviousTable();
        }
        return null;
    }

    private void checkFields(LinkedHashMap<String,SemanticType> functionFields,ArrayList<ExpressionStatement> functionCallfields ) throws TypeErrorException {
        int i=0;
        for (String key : functionFields.keySet()) {
            if(i>=functionCallfields.size()){
                throw new TypeErrorException("Missing fields");
            }
            SemanticType value = functionFields.get(key);
            checkExpression(this.globalTable,functionCallfields.get(i),value.getType());
            i++;
        }
    }


    private void checkOperatorType(Operator op, String type) throws TypeErrorException {
        Token typeTokenType = op.getSymbol().getType();

        if (type.equals("bool") && typeTokenType != Token.LogicalOperator) {
            throw new TypeErrorException("Operator: " + op + " doesn't match the expression type (" + type + ")");
        }

        if ((type.equals("int") || type.equals("float")) &&
                !(typeTokenType == Token.AdditiveOperator || typeTokenType == Token.MultiplicativeOperator)) {
            throw new TypeErrorException("Operator: " + op + " doesn't match the expression type (" + type + ")");
        }

        if (type.equals("string") && !op.getSymbol().getValue().equals("+")) {
            throw new TypeErrorException("Operator: " + op + " doesn't match the expression type (" + type + ")");
        }


    }

    /*
    Il metodo addStruct controlla nel seguente ordine:
        1- non è gia stata creata una stuct con lo stesso nome
        2- per ogni parametro, che questo sia un tipo di base, o abbia
           un tipo che appartiene gia ad una struct definita
        3-che non ci siano attributi con nomi uguali
     */
    private void addStruct(Struct s) throws TypeErrorException {
        String structName= s.getIdentifier().getValue();
        if(structTable.get(structName)!=null){ //controllo 1
            throw new TypeErrorException("Struct "+structName +" already defined "+ "(line "+s.getIdentifier().getLine()+")");
        }
        Map<String,SemanticType> fields= new LinkedHashMap<>();
        ArrayList<VariableDeclaration> structParameters= s.getVariableDeclarations();
        for(VariableDeclaration var: structParameters){
            Type varType=var.getType();
            if(varType.getSymbol().getType().equals(Token.BasedType) || //controllo 2
                    (varType.getSymbol().getType().equals(Token.Identifier) && structTable.get(var.getType().toString())!=null )){
                if(fields.get(var.getIdentifier().toString())!=null){ //controllo 3
                    throw new TypeErrorException("Struct "+structName+" contains duplicate fields name: "+var.getIdentifier().getIdentifier()+ " (line "+var.getType().getSymbol().getLine()+")");
                }
                fields.put(var.getIdentifier().getIdentifier(),new SemanticType(varType.getSymbol().getValue()));
            }
            else{
                throw new TypeErrorException("Type "+var.getType() +" not defined ");
            }
        }
        this.structTable.add(structName,new SemanticRecordType("",fields));
    }

    //TODO Gestire il type dei vettori


    public void addGlobalVariable(GlobalVariable gV)  throws TypeErrorException {
        String id=gV.getIdentifier().getValue(); //Si preleva l'identifier
        Type type=gV.getType(); //Si preleva il tipo
        if(this.globalTable.get(id)!=null){
            throw new TypeErrorException("Constant "+id +" already defined "+ "(line "+gV.getIdentifier().getLine()+")");
        }
        checkExpression(this.globalTable,gV.getValue(),type.getSymbol().getValue());
        this.globalTable.add(id,new SemanticType(type.getSymbol().getValue()));
    }

    /*MODIFICA 9 sera, per poter accedere all'interno delle functionCall ai parametri dei metodi
    i fields non sono contenuti all'interno di una SymbolTable come faceva intendere le slide
    ma all'interno della SymbolTable principale caratterizzata da un SemanticRecordType questo
    consente di controllare i paramentri all'interno delle FunctionCall esterne al metodo
     */


    private void addProcedure(Procedure p) throws TypeErrorException {
        Type type=p.getReturnType(); //Si preleva il tipo
        String id= p.getName().getValue();
        if(this.globalTable.get(id)!=null){ //Id gia in uso
            throw new TypeErrorException(id +" already used "+ "(line "+p.getName().getLine()+")");
        }
        //Si aggiungono i parametri della procedura ad un TreeMap
        //SymbolTable procedureSymbolTable= new SymbolTable(this.globalTable, new TreeMap<String,SemanticType>());
        LinkedHashMap<String, SemanticType> procedureFields = new LinkedHashMap<>();
        ArrayList<VariableDeclaration> variableDeclarations=p.getParameters_of_the_procedure();
        for(VariableDeclaration v : variableDeclarations){
            if(!(procedureFields.get(v.getIdentifier().getIdentifier())==null)){
                throw new TypeErrorException("Identifier "+v.getIdentifier().getIdentifier() +" already defined ");
            }
            //Se il tipo della variabile non è di base, e non viene trovata una struct del tipo --> Errore
            if(!v.getType().getSymbol().getType().equals(Token.BasedType) &&
                this.structTable.get(v.getType().getSymbol().getValue())==null ){
                throw new TypeErrorException("Type "+v.getType().getSymbol().getValue() +" not defined ");
            }
            procedureFields.put(v.getIdentifier().getIdentifier(),
                    new SemanticType(v.getType().getSymbol().getValue()));
        }

        this.globalTable.add(id,new SemanticRecordType(type.getSymbol().getValue(),procedureFields));

        checkBlock(id, p.getBody(), this.globalTable);
    }


    private void checkBlock(String name, Block b, SymbolTable precSymbolTable) throws TypeErrorException {
        ArrayList<ASTNode> statements= b.getStatements();
        SymbolTable blockSymbolTable;
        if(name!=null){
            blockSymbolTable= popolateMethodSymbolTable(name);
        }else{
            blockSymbolTable=new SymbolTable(precSymbolTable);
        }
        Iterator<ASTNode> it= statements.iterator(); //Si itera sull'AST del Block creato dal Parser
        while(it.hasNext()){
            ASTNode next = it.next();
            //System.out.println(next);
            if(next instanceof VariableInstantiation variableInstantiation){
                String id=variableInstantiation.getIdentifier().getIdentifier();
                //Si controlla che non esista una variabile con stesso nome nei parametri e nel corpo
                if(blockSymbolTable.get(id)!=null) {
                    throw new TypeErrorException("Variable "+ id + "already defined!");
                }
                checkType(variableInstantiation.getType());
                checkExpression(blockSymbolTable,variableInstantiation.getRight_side(),variableInstantiation.getType().getSymbol().getValue());
                System.out.println("Adding: "+ id);
                blockSymbolTable.add(id,new SemanticType(variableInstantiation.getType().toString()));
            } //è necessario l'else if in quanto una variableInstantiation è anche una variableDeclaration
            else if(next instanceof VariableDeclaration variableDeclaration){
                String id=variableDeclaration.getIdentifier().getIdentifier();
                //Si controlla che non esista una variabile con stesso nome nei parametri e nel corpo
                if(blockSymbolTable.get(id)!=null) {
                    throw new TypeErrorException("Variable "+ id + " already defined!");
                }
                System.out.println("Adding: "+ id);
                blockSymbolTable.add(id,new SemanticType(variableDeclaration.getType().toString()));

            }
            if(next instanceof VariableAssigment variableAssigment){
                String type = getAssignmentType(blockSymbolTable,variableAssigment.getVariable());
                checkExpression(blockSymbolTable,variableAssigment.getRight_side(),type);
            }
            if(next instanceof ReturnStatement returnStatement){
                checkExpression(blockSymbolTable,returnStatement.getExpression(),this.globalTable.get(name).getType());
            }
            if(next instanceof ForStatement forStatement){
                //TODO Gestire caso loop for(,,)
                //Si controlla che l'assegnamento del valore all'indice avvenga corretamente
                VariableAssigment start=forStatement.getStart();
                String type=getTypeFromSymbolsTable(blockSymbolTable,((VariableReference)start.getVariable()).getIdentifier()).getType();
                if(!type.equals("int")){
                    throw new TypeErrorException("Variable int required in the for statement!");
                }
                checkExpression(blockSymbolTable, start.getRight_side(),type);
                //Si controlla la condizione di uscita
                ExpressionStatement endCondition = forStatement.getEndCondition();
                //Si controlla l'update
                VariableAssigment update= forStatement.getUpdate();
                checkExpression(blockSymbolTable,update.getRight_side(),getAssignmentType(blockSymbolTable,update.getVariable()));
                checkBlock(null,forStatement.getBlock(),blockSymbolTable);
            }
            if(next instanceof IfStatement ifStatement){
                ExpressionStatement condition=ifStatement.getIfCondition();
                checkBlock(null,ifStatement.getIfBlock(),blockSymbolTable);
                if(next instanceof IfElseStatement ifElseStatement){
                    checkBlock(null, ifElseStatement.getElse_block(),blockSymbolTable);
                }
            }
            if(next instanceof WhileStatement whileStatement){
                ExpressionStatement condition=whileStatement.getExitCondition();
                checkBlock(null, whileStatement.getBlock(),blockSymbolTable);
            }

        }
        System.out.print(blockSymbolTable);

    }



    private void checkType(Type type) throws TypeErrorException {
        if(!type.getSymbol().getType().equals(Token.BasedType) &&
                this.structTable.get(type.getSymbol().getValue())==null ){
            throw new TypeErrorException("Type "+type.getSymbol().getValue() +" not defined ");
        }
    }

    private String getAssignmentType(SymbolTable blockSymbolTable,ExpressionStatement exp) throws TypeErrorException{
        if(exp instanceof VariableReference variableReference){ //Caso semplice
            SymbolTable prevTable=blockSymbolTable;
            while(prevTable!=null){
                if(prevTable.get(variableReference.getIdentifier())!=null){
                    return prevTable.get(variableReference.getIdentifier()).getType();
                }
                prevTable=prevTable.getPreviousTable();
            }
            throw new TypeErrorException("Variable " + variableReference.getIdentifier() + " not defined!!");
        }

        if(exp instanceof StructAccess){
             return getStructAccessType(blockSymbolTable, exp, false);
        }

        return  null;
    }


    private String getStructAccessType(SymbolTable symbolTable,ExpressionStatement exp, boolean structBool) throws TypeErrorException{

        if(exp instanceof VariableReference variableReference){ //Caso semplice
            if(symbolTable.get(variableReference.getIdentifier())==null) {
                throw new TypeErrorException("Variable " + variableReference.getIdentifier() + " not defined!!");
            }
            return symbolTable.get(variableReference.getIdentifier()).getType();
        }

        if(exp instanceof StructAccess structAccess){
            VariableReference variableAssigment= (VariableReference) structAccess.getLeftPart();
            //Se è la prima chiamata del metodo
            if(!structBool){
                //Si verifica che esiste una istanza della struct
                if(getTypeFromSymbolsTable(symbolTable,variableAssigment.getIdentifier())==null){
                    throw new TypeErrorException("Struct istance "+variableAssigment.getIdentifier()+" not defined!!");
                }
                //Si verifica che la struct esista
                SemanticType structType=getTypeFromSymbolsTable(symbolTable,variableAssigment.getIdentifier());
                if(this.structTable.get(structType.getType())==null){
                    throw new TypeErrorException("Struct "+variableAssigment.getIdentifier()+" not defined!!");
                }
                SymbolTable newSymbolTable = popolateStuctSymbolTable(structType.getType());
                return getStructAccessType(newSymbolTable,structAccess.getRightPart(),true);
            }else { //Se la parte sinistra esiste come struct, si richiama il metodo ricorsivamente sulla parte destra
                //finche non si ha una variable reference
                if (symbolTable.get(variableAssigment.getIdentifier()) == null) {
                    throw new TypeErrorException("Struct " + variableAssigment.getIdentifier() + " not defined!!");
                }
                SymbolTable newSymbolTable = popolateStuctSymbolTable(symbolTable.get(variableAssigment.getIdentifier()).getType());
                return getStructAccessType(newSymbolTable,structAccess.getRightPart(),true);
            }
        }

        return null;
    }


    private SymbolTable popolateMethodSymbolTable(String name){
        LinkedHashMap<String,SemanticType> functionFields= (LinkedHashMap<String, SemanticType>) ((SemanticRecordType) this.globalTable.get(name)).getFields();
        SymbolTable ret= new SymbolTable(this.globalTable);
        for (String key : functionFields.keySet()) {
            SemanticType value = functionFields.get(key);
            ret.add(key,value);
        }
        return ret;
    }

    private SymbolTable popolateStuctSymbolTable(String name){

        LinkedHashMap<String,SemanticType> functionFields= (LinkedHashMap<String, SemanticType>) ((SemanticRecordType) this.structTable.get(name)).getFields();
        SymbolTable ret= new SymbolTable();
        for (String key : functionFields.keySet()) {
            SemanticType value = functionFields.get(key);
            //System.out.println(key+ "-"+ value);
            ret.add(key,value);
        }
        return ret;
    }


}



