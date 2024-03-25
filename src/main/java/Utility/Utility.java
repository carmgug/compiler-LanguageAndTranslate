package Utility;

public final class Utility {

    private Utility() {}

    public static String indentedString(String jsonString){
        // Indentazione desiderata
        String indent = "    "; // 4 spazi per l'indentazione

        // Indentazione iniziale
        StringBuilder indentedString = new StringBuilder();
        int indentationLevel = 0;

        // Analizza la stringa carattere per carattere
        for (char c : jsonString.toCharArray()) {
            if (c == '{') {
                indentedString.append(c).append("\n"); // Aggiunge la graffa aperta e va a capo
                indentationLevel++; // Incrementa il livello di indentazione
                for(int i=0; i<indentationLevel; i++) indentedString.append(indent); // Aggiunge l'indentazione
            } else if (c == '}') {
                indentedString.append("\n"); // Va a capo prima della graffa chiusa
                indentationLevel--; // Decrementa il livello di indentazione
                for(int i=0; i<indentationLevel; i++) indentedString.append(indent); // Aggiunge l'indentazione
                indentedString.append(c); // Aggiunge la graffa chiusa
            }else if(c == '['){
                indentedString.append(c).append("\n"); // Aggiunge la graffa aperta e va a capo
                indentationLevel++; // Incrementa il livello di indentazione
                for(int i=0; i<indentationLevel; i++) indentedString.append(indent); // Aggiunge l'indentazione
            }else if(c==']'){
                indentedString.append("\n"); // Va a capo prima della graffa chiusa
                indentationLevel--; // Decrementa il livello di indentazione
                for(int i=0; i<indentationLevel; i++) indentedString.append(indent); // Aggiunge l'indentazione
                indentedString.append(c); // Aggiunge la graffa chiusa
            }
            else if (c == ',') {
                indentedString.append(c).append("\n"); // Aggiunge la virgola e va a capo
                for(int i=0; i<indentationLevel; i++) indentedString.append(indent); // Aggiunge l'indentazione
            } else {
                indentedString.append(c); // Aggiunge il carattere corrente
            }
        }

        // Stampa la stringa indentata
        return indentedString.toString();
    }
}
