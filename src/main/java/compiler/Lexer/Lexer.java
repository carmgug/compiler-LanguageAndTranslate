package compiler.Lexer;
import java.io.IOException;
import java.io.Reader;
import java.util.*;


public class Lexer {

    private Reader input;
    private Queue<Integer> queue;

    private Map<String, Token[]> rules;

    public Lexer(Reader input) {
        this.input=input;
        this.queue=new ArrayDeque<Integer>();
        this.rules=new HashMap<>();
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



            }
            else if(isDigit(c)){
                //Arrivati qua dentro sappiamo che c è un numero
                //ci aspettiamo che i prossimi caratteri siano numeri
                //ci dobbiamo fermare quando non abbiamo più numeri o .;
                boolean point=false;
                StringBuilder sb=new StringBuilder();
                sb.append((char) c);
                //Dobbiamo leggere i caratteri successivi
                while(true){
                    c=input.read();
                    if(c>'0' && c<'9'){
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
                Token floatNumber = Token.FloatNumber;
                Token intNumber = Token.IntNumber;
                if(intNumber.match(sb.toString())){
                    return new Symbol(intNumber,sb.toString());
                }
                if(floatNumber.match(sb.toString())){
                    return new Symbol(floatNumber, sb.toString());
                }
            } else if (isASlide(c)) {
                //Si trova '/'
                StringBuilder sb=new StringBuilder();
                sb.append((char) c);
                boolean commentFounded=false;
                boolean preparingExit=false;
                while(true){
                    c=input.read();
                    if(c=='/' && !commentFounded){ //Siamo in un commento
                        commentFounded=true;
                        sb.append((char)c);
                    }
                    else if(commentFounded){ //Dobbiamo aggiungere tutti i caratteri fintanto che non si trova \n
                        if(c=='\\' && !preparingExit){ //Se si trova '\' si cerca la \
                            preparingExit=true;
                            sb.append((char)c);
                        }
                        else if(preparingExit && c=='n'){ //Condizione di uscita
                            //Si è trovato lo \n
                            sb.append((char)c);
                            Token comment = Token.Comment;
                            return new Symbol(comment, sb.toString());
                        }
                        else if(preparingExit && c!='n'){
                            preparingExit=false;
                            sb.append((char) c);
                        }else{
                            sb.append((char)c);
                        }
                    }else{ // si ha uno special character /
                        queue.add(c);
                        return new Symbol(Token.ArithmeticOperator,"/");
                    }
                }

            } else if(isOperator(c)){
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
                //&!

            }
            else{ //Si possono avere due possibili situazioni: -SpecialCharactareUnknownToken
                StringBuilder sb=new StringBuilder();
                sb.append((char) c);
                for(Token tk:rules.get("isOperator")){
                    if(tk.match(sb.toString()))
                        return new Symbol(tk,sb.toString());
                }
            }
            //Questa cosa va fatta fare al parser o al lexer?
            return new Symbol(Token.UnknownToken, String.valueOf((char) c));
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
}
