package oracle.wallet.maintenance;

public class Cipher64 {
    private String message = "";
    private int shift = 0;
    private static int LIMIT = 91;

    public Cipher64(String message, int shift){
        this.message = message;
        this.shift = shift;

        if (this.shift > LIMIT) {
            this.shift %= LIMIT;
        }
    }

    public String encrypt() {
        StringBuilder rot = new StringBuilder();
        char tempChar = '\0';
        char chAt = '\0';

        for (int i = 0; i < this.message.length(); i++) {
            chAt = (this.message.charAt(i));
            tempChar = (char)(this.message.charAt(i) + (shift));
            if (chAt == '=') {
                rot.append(chAt);
            } else if (tempChar == '=') {
                rot.append('"');
            } else if (tempChar > '~'){
                tempChar = (char)(chAt - (LIMIT-this.shift+1));
                if (tempChar == '='){
                    rot.append('!');
                } else {
                    rot.append(tempChar);
                }
                //System.out.println(this.message.charAt(i) + ": " + Integer.toString((int)tempChar) + ": shift: " + Integer.toString(this.shift));
            } else {
                rot.append(tempChar);
            }

        }
        return rot.toString();
    }

    public String decrypt(){
        StringBuilder decode = new StringBuilder();
        char tempChar = '\0';
        char chAt = '\0';

        for (int j = 0; j < this.message.length(); j++) {
            chAt = (this.message.charAt(j));
            tempChar = (char)(this.message.charAt(j) - shift);
            if (chAt == '=') {
                decode.append(chAt);
            } else if (chAt == '!') {
                tempChar += (LIMIT + ((int)'=') - ((int)'!') + 1);
                decode.append(tempChar);
            } else if (chAt == '"') {
                tempChar += (((int)'=') - ((int)'"'));
                decode.append(tempChar);
            } else if (tempChar < '#'){
                tempChar = (char)(chAt + (LIMIT-this.shift+1));
                decode.append(Character.toString(tempChar));
            } else if (tempChar > '~') {
                tempChar += LIMIT+1;
                decode.append(tempChar);
            } else {
                decode.append(tempChar);
            }
        }
        return decode.toString();
    }
}
