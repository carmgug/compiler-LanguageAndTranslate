package compiler.Lexer;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;



public class Lexer {

    private Reader input;
    private Queue<Integer> queue;


    public Lexer(Reader input) {
        this.input=input;
        queue=new ArrayDeque<Integer>();


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
                Token basedType = Token.BasedType;
                Token keyword = Token.Keywords;
                Token identifier=Token.Identifier;
                if(basedType.match(sb.toString())){
                    return new Symbol(basedType,sb.toString());
                }
                if(keyword.match(sb.toString())){
                    return new Symbol(keyword, sb.toString());
                }
                if(identifier.match(sb.toString())){
                    return new Symbol(identifier, sb.toString());
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
                    if(isOperator(c)){
                        sb.append((char) c);
                    }
                    else{
                        queue.add(c);
                    }
                }
                Token[] tokens = {Token.AssignmentOperator,
                        Token.ArithmeticOperator,
                        Token.ComparisonOperator,
                        Token.LogicalOperator,
                        Token.IncrementOperator};
                for(Token tk:tokens){
                    if(tk.match(sb.toString()))
                        return new Symbol(tk,sb.toString());
                }
                return new Symbol(Token.UnknownToken, sb.toString());
            }
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
        return curr_elem>='1' && curr_elem<='9';
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
