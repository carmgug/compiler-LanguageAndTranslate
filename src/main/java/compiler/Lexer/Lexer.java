package compiler.Lexer;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.rmi.UnexpectedException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;


public class Lexer {

    /**
     * The class logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(Lexer.class.getName());
    private Reader input;
    private Queue<Integer> queue;

    private Map<String, Token[]> rules;

    private int curr_line;

    private boolean debugMode;

    public Lexer(Reader input) {
        this.input=input;
        this.queue=new ArrayDeque<Integer>();
        this.curr_line=0;
        this.rules=new HashMap<>();
        this.debugMode=false;
        LOGGER.isEnabled(Level.DEBUG);
        setRules();
    }

    public Lexer(Reader input, boolean debugMode) {
        this.input=input;
        this.queue=new ArrayDeque<Integer>();
        this.curr_line=0;
        this.rules=new HashMap<>();
        this.debugMode=false;
        setRules();
    }

    private void setRules(){
        this.rules.put("isLetter",new Token[]{Token.BasedType,Token.Keywords,Token.BooleanValue,Token.Identifier});
        this.rules.put("isDigit",new Token[]{Token.FloatNumber, Token.IntNumber});
        this.rules.put("isOperator",new Token[]{Token.AssignmentOperator,Token.ArithmeticOperator,Token.ComparisonOperator,
                Token.LogicalOperator,
                Token.IncrementOperator});
        this.rules.put("SpecialCharacter", new Token[]{Token.SpecialCharacter});
    }
    
    public Symbol getNextSymbol() throws IOException {
        LOGGER.log(Level.DEBUG,"ciao");

        while(true) {
            int c = 0;
            //Si controlla se nel getSymbol precedente è stato consumato un carattere in piu
            //che deve essere elaborato
            if(!queue.isEmpty()){
                c=queue.poll();
            } else { //Altrimenti si legge dal reader
                c=input.read();
            }
            if (c == -1) { //EOF Reached
                return new Symbol(Token.EOF, "");
            }
            else if(isWhitespace(c)){

                continue;
            }
            else if(isLetter(c)){

                Symbol s=letterHandler(c);
                if(s!=null) return s;
                //altrimenti non ha matchato
            }
            else if(isDigit(c)){

                Symbol s = digitHandler(c);
                if(s!=null) return s;
            } else if (isASlide(c)) { //potrebbe essere un commento ho una divisione se // commento se / allora commento
                Symbol s=slideHandler(c);
                if(s.isTypeof("Comment")){
                    continue;
                }
                return s;
            } else if (isABackSlash(c)){
                Symbol s =blackSlashHandler(c);
                if(s!=null && (s.isTypeof("NewLine") || s.isTypeof("Indent"))) {
                    continue;
                } //dont return and go on
                //otherwise
            } else if(isOperator(c)){
                Symbol s= operatorHandler(c);
                if(s!= null) return s;
            }
            else{
                StringBuilder sb=new StringBuilder();
                sb.append((char) c);
                for(Token tk:rules.get("SpecialCharacter")){
                    if(tk.match(sb.toString())) {
                        return new Symbol(tk, sb.toString());
                    }
                }
            }
            Symbol curr_symbol=new Symbol(Token.UnknownToken, String.valueOf((char) c));
            //throw new IOException(" Unrecognized tokens "+curr_symbol+" at line " +curr_line);
            System.out.println(curr_symbol);
        }
    }


    private boolean isWhitespace(int c){
        return ((char) c)==(' ');
    }
    private boolean isLetter(int c){
        char curr_elem=(char) c;
        return (curr_elem >= 'a') && curr_elem <= 'z' || //lower case
                curr_elem >= 'A' && curr_elem <= 'Z'  || //upper case
                curr_elem == '_' ; //underline
    }

    private boolean isDigit(int c){
        char curr_elem= (char) c;
        return curr_elem>='0' && curr_elem<='9';
    }

    private boolean isASlide(int c){
        char curr_elem= (char) c;
        return curr_elem=='/';
    }

    private boolean isABackSlash(int c){
        return ((char)c)=='\\';
    }

    private boolean isSpecialCharacter(int c){
        //=, +, -, *, /, %, ==, <>, <, >, <=, >=, (, ), {, }, [, ], . (dot), &&, || ; ,(comma)
        //==  <> <= >= && ||
        //<\\w+> | ( | ) | \\ { | \\ } | [ | ] | . | \ \ | | , | ; " , "SpecialCharacter"),
        char curr_elem= (char) c;
        return false;
    }

    private boolean isOperator(int c) {
        char[] op = {'=', '+', '-', '*', '/', '%', '<', '>','!','&'};

        char curr_elem = (char) c;
        for (char e : op) {
            if (curr_elem == e) return true;
        }
        return false;
    }


    private Symbol slideHandler(int c) throws IOException {
        //Si trova '/'
        StringBuilder sb=new StringBuilder();
        sb.append((char) c);
        boolean commentFounded=false;
        boolean preparingExit=false;
        while(true){
            c=input.read();

            if((char)c=='/' && !commentFounded){ //Siamo in un commento abbiamo trovato //
                commentFounded=true;
                sb.append((char)c);
            }
            else if(commentFounded){ //Dobbiamo aggiungere tutti i caratteri fintanto che non si trova \n

                if((char)c=='\\' && !preparingExit){ //Se si trova '\' si cerca la n
                    preparingExit=true;
                    sb.append((char)c);
                }
                else if(preparingExit && (char)c=='n'){ //Condizione di uscita

                    //Si è trovato lo \n
                    sb.append((char)c);
                    Token comment = Token.Comment;
                    //Comment finito allora linea successiva
                    curr_line++;
                    System.out.println(" Nuova Linea");
                    return new Symbol(comment, sb.toString());
                }
                else if(preparingExit && (char)c!='n'){
                    preparingExit=false;
                    sb.append((char) c);
                }else{
                    sb.append((char)c);
                }
            }else{ // si ha uno arithmeticOperator /
                queue.add(c);
                return new Symbol(Token.ArithmeticOperator,"/");
            }
        }

    }
    private Symbol operatorHandler(int c) throws IOException {

        StringBuilder sb=new StringBuilder();
        sb.append((char) c);
        //Dobbiamo leggere i caratteri successivi
        if(c=='=' || c=='<' || c== '>' || c=='&' || c=='|' || c=='+'){

            c=input.read();

            if(isOperator(c) &&
                    (sb.charAt(0)==((char)c) ||  //== && || ++
                            ((sb.charAt(0)=='<' || sb.charAt(0)=='>') && ((char)c)=='=')
                    ) ){
                sb.append((char) c);
            }

            else{
                queue.add(c);
            }
        }
        for(Token tk:rules.get("isOperator")){
            if(tk.match(sb.toString()))
                return new Symbol(tk,sb.toString());
        }


        return null;
    }

    private Symbol blackSlashHandler(int c) throws IOException{
        //ok i Found a BlackSlash so if the next character is a n i need to go to the nextline
        System.out.println((char)c);
        c=input.read();
        System.out.println((char)c);
        if((char)c == 'n') {// is a n
            curr_line++;
            return new Symbol(Token.NewLine,"\n");
        }
        //ora potrebbe essere una t e in quel caso va consumata altrimenti va messo nella prossima queue
        if((char) c=='t') return new Symbol(Token.Indent,"\t");
        //negli altri casi va messo in queue
        queue.add(c);
        return null;
    }

    private Symbol digitHandler(int c) throws IOException{
        //Arrivati qua dentro sappiamo che c è un numero
        //ci aspettiamo che i prossimi caratteri siano numeri
        //ci dobbiamo fermare quando non abbiamo più numeri o .;
        boolean point=false;
        StringBuilder sb=new StringBuilder();
        sb.append((char) c);
        //Dobbiamo leggere i caratteri successivi
        while(true){
            c=input.read();
            if(c>='0' && c<='9'){
                sb.append((char)c);
            }
            else if (c=='.' && !point) {
                point=true;
                sb.append((char)c);
            }
            else{
                queue.add(c);
                break;
            }
        }
        for(Token tk:rules.get("isDigit")){
            if(tk.match(sb.toString())) {
                return new Symbol(tk, sb.toString());
            }
        }

        return null;
    }

    private Symbol letterHandler(int c) throws IOException{

        StringBuilder sb=new StringBuilder();
        sb.append((char) c);
        while(true){
            c=input.read();
            if(isLetter(c)){
                sb.append((char)c);
            }
            else if(isDigit(c)){
                sb.append((char)c);
            }
            else{
                queue.add(c);
                break;
            }
        }
        for(Token tk:rules.get("isLetter")){
            if(tk.match(sb.toString()))
                return new Symbol(tk,sb.toString());
        }

        return null; //No match

    }





}
