package compiler.Lexer;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.rmi.UnexpectedException;
import java.util.*;


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
        this.curr_line=1;
        this.rules=new HashMap<>();
        this.debugMode=false;
        setRules();
    }

    public Lexer(Reader input, boolean debugMode) {
        this.input=input;
        this.queue=new ArrayDeque<Integer>();
        this.curr_line=1;
        this.rules=new HashMap<>();
        this.debugMode=debugMode;
        setRules();
    }


    private void setRules(){

        this.rules.put("isLetter",new Token[]{Token.IntType,Token.FloatType,Token.BoolType,Token.StringType,Token.Final,Token.Struct,Token.For,Token.Return,Token.Void,
                Token.Def,Token.While,Token.If,Token.Else,Token.BooleanValue,Token.Identifier});
        this.rules.put("isDigit",new Token[]{Token.FloatNumber, Token.IntNumber});
        this.rules.put("isOperator",new Token[]{Token.AssignmentOperator,Token.AdditiveOperator,Token.MultiplicativeOperator,Token.ComparisonOperator,
                Token.LogicalOperator,Token.IncrementOperator});
        this.rules.put("SpecialCharacter", new Token[]{Token.OpeningParenthesis,Token.ClosingParenthesis,Token.OpeningCurlyBrace,
                Token.ClosingCurlyBrace,Token.OpeningSquareBracket,Token.ClosingSquareBracket,Token.Dot,Token.Comma,Token.DoubleQuote,Token.Semicolon});
    }
    
    public Symbol getNextSymbol() throws IOException {


        while(true) {
            int c = 0;
            //if in the before getSymbol has been consumed an extra character that
            // need to be processed, take it from the queue.
            if(!queue.isEmpty()){
                c=queue.poll();
                if(debugMode) LOGGER.log(Level.DEBUG,"A Character has been consumed from the queue");
            } else { //Otherwise the character will be extracted from the reader
                c=input.read();
            }
            if (c == -1) { //The EOF has been Reached
                Symbol s= new Symbol(Token.EOF, "",curr_line);
                if(debugMode) LOGGER.log(Level.DEBUG,"The end of the file has been reached -->"+ s.toString()+ " - at line "+ curr_line);
                return s;
            }
            else if(isWhitespaceOrIndent(c)){
                continue;//whitespace is ignored
            }
            else if(isLetter(c)){//A letter has been detected so it's possibile to match a //BaseType,KeyWords ecc.
                Symbol s=letterHandler(c);
                if(debugMode) LOGGER.log(Level.DEBUG,s.toString()+ " - at line "+ curr_line);
                return s;

            }
            else if(isDigit(c)){//A digit has been detected as first character so it's possible to match a
                Symbol s= digitHandler(c);
                if(debugMode) LOGGER.log(Level.DEBUG,s.toString()+ " - at line "+ curr_line);
                return s;

            } else if (isASlide(c)) {
                //A / as been found, so it's possibile to have a comment (//) or a division operator(/)
                Symbol s=slideHandler(c);
                if(s.isTypeof("Comment")){
                    LOGGER.log(Level.DEBUG,"Comment at line "+ (curr_line-1));
                    continue; //don't return Comment to the Parser
                }
                return s;//is a ArithmeticOperator
            }else if (isEndOfTheline(c)){ //An end of line has been founded
                Symbol s =endOfTheLineHandler(c);
                if(s!=null && (s.isTypeof("NewLine"))) {
                    continue;
                } //don't return and go on a new line
            } else if(isOperator(c)){ //An operator has been founded
                Symbol s= operatorHandler(c);
                if(s!= null) {
                    if(debugMode) LOGGER.log(Level.DEBUG,s.toString()+ " - at line "+ curr_line);
                    return s;
                }
            }else if(isQuotationMark(c)){
                Symbol s= stringHandler(c);
                if(debugMode) LOGGER.log(Level.DEBUG,s.toString()+ " - at line "+ curr_line);
                return s;
            } else if(isSpecialCharacter(c)){
                for(Token tk:rules.get("SpecialCharacter")) {
                    if (tk.isMatch(String.valueOf((char) c))) {
                        Symbol s = new Symbol(tk, String.valueOf((char) c), curr_line);
                        if (debugMode) LOGGER.log(Level.DEBUG, s.toString() + " - at line " + curr_line);
                        return s;
                    }
                }
            }
            Symbol curr_symbol=new Symbol(Token.UnknownToken, String.valueOf((char) c),curr_line);
            throw new IOException(" Unrecognized tokens "+curr_symbol+" at line " +curr_line);
        }
    }


    private boolean isQuotationMark(int c){
        return c=='"';

    }


    private boolean isEndOfTheline(int c){
        return (((char) c)=='\r' || ((char) c)=='\n');
    }


    private boolean isWhitespaceOrIndent(int c){
        return (((char) c)==(' ') || ((char) c)=='\t');
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
        char[] specialCharacters ={'(',')','{','}','[',']','.',';',',','"'};
        //<\\w+> | ( | ) | \\ { | \\ } | [ | ] | . | \ \ | | , | ; " , "SpecialCharacter"),
        char curr_elem= (char) c;
        for (char e : specialCharacters) {
            if (curr_elem == e) return true;
        }
        return false;
    }

    private boolean isOperator(int c) {
        char[] op = {'=', '+', '-', '*', '/', '%', '<', '>','!','&','|'};

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
            if((char)c=='/' && !commentFounded){ //We found the second / of the start of a comment
                commentFounded=true;
                sb.append((char)c);
            }
            else if(commentFounded){

                if(((char)c)=='\r'){//We found an \r so if we are in windows we are going outside
                    sb.append((char)c);
                    c=input.read();
                    if((char)c=='\n'){
                        sb.append((char)c);
                        Token comment = Token.Comment;
                        Symbol s=new Symbol(comment, sb.toString(),curr_line);
                        //Comment finito allora linea successiva
                        curr_line++;
                        return s;
                    }
                    sb.append((char)c);
                }
                else if((char)c=='\n'){//So we are in unix
                    sb.append((char)c);
                    Token comment = Token.Comment;
                    //Find a comment so increment line;
                    Symbol s=new Symbol(comment, sb.toString(),curr_line);
                    curr_line++;
                    return s;
                } else{
                    sb.append((char)c);
                }
            }else{ // si ha uno arithmeticOperator /
                queue.add(c);
                return new Symbol(Token.MultiplicativeOperator,"/",curr_line);
            }
        }

    }
    private Symbol operatorHandler(int c) throws IOException {
        StringBuilder sb=new StringBuilder();
        //!=
        sb.append((char) c);

        if(c=='=' || c=='&' || c=='|' || c=='+'){
            //We need to consume the next character

            c=input.read();

            if(isOperator(c) && (sb.charAt(0)==((char)c))) {
                sb.append((char) c);
            }
            else{
                queue.add(c);
            }
        } else if(c=='<' || c== '>' || c=='!'){
            c=input.read();

            if( isOperator (c) && ((char)c)=='=') {
                sb.append((char)c);
            }else{
                queue.add(c);
            }
        }
        for(Token tk:rules.get("isOperator")){
            if(tk.isMatch(sb.toString())) return new Symbol(tk,sb.toString(),curr_line);

        }
        return null;
    }

    private Symbol endOfTheLineHandler(int c) throws IOException{
        //Ok if i'm on windows I found a \r (CarriageReturn) so i expected to find a \n newLine
        //In the other case i found a \n newLine so go on;
        if((char) c=='\n') {curr_line++; return new Symbol(Token.NewLine,"\\n",curr_line-1);}
        //So I found a \r (CarriageReturn) so i expected to find a \n newLine
        c=input.read();
        if((char)c =='\n'){
            curr_line++;
            return new Symbol(Token.NewLine,"\\n",curr_line-1);
        }
        queue.add(c);
        return null;
    }

    private Symbol digitHandler(int c) throws IOException{
        //Arrivati qua dentro sappiamo che c è un numero
        //ci aspettiamo che i prossimi caratteri siano numeri
        //ci dobbiamo fermare quando non abbiamo più numeri o .;
        int count_point=0;
        StringBuilder sb=new StringBuilder();
        sb.append((char) c);
        //Dobbiamo leggere i caratteri successivi
        while(true){
            c=input.read();
            if(c>='0' && c<='9'){
                sb.append((char)c);
            }
            else if (c=='.' && count_point==0) {
                count_point+=1;
                sb.append((char)c);
            }
            else{
                if(sb.charAt(sb.length()-1)=='.'){
                    sb.deleteCharAt(sb.length()-1);
                    queue.add(((int)'.'));
                }
                queue.add(c);
                break;
            }
        }
        if(count_point==1) return new Symbol(Token.FloatNumber,sb.toString(),curr_line);
        if(count_point==0) return new Symbol(Token.IntNumber,sb.toString(),curr_line);
        //if it's not a Float Number or a IntNumber so ther is something wrong
        throw new IOException(" Unrecognized tokens "+new Symbol(Token.UnknownToken,sb.toString(),curr_line)+" at line " +curr_line);
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
        //Token.BasedType,Token.Keywords,Token.BooleanValue,Token.Identifier


        for(Token tk:rules.get("isLetter")){
            if (tk.name().equals("Identifier")) {//Identifier is the last rule so we here if the preovius rules doesnt match
                return new Symbol(tk, sb.toString(), curr_line);
            } else {//BasedType,KeyWords,BooleanValue,Return
                if (tk.isMatch(sb.toString())) return new Symbol(tk, sb.toString(), curr_line);
            }
        }
        return null; //No match

    }

    private Symbol stringHandler(int c) throws IOException {
        //A open string is detected
        //So the next character will be in a string to until a " has been reached
        StringBuilder sb=new StringBuilder();
        sb.append((char)c);
        c = input.read();
        while(!isQuotationMark(c) && c!=-1){
            sb.append((char)c);
            c=input.read();
        }
        if(isQuotationMark(c)){
            sb.append((char)c);
            Symbol s=new Symbol(Token.String,sb.toString(),curr_line);
            return s;
        }

        throw new IOException(" Unrecognized tokens "+new Symbol(Token.UnknownToken,sb.toString(),curr_line)+" at line " +curr_line);

    }






}
