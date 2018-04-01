package oracle.wallet.maintenance;

import java.security.SecureRandom;

public class PasswordGenerator {
    private static SecureRandom random = new SecureRandom();

    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBER = "1234567890";
    private static final String SPECIAL = "^!%=?{[]}+~#_.:,<>|\\";
    private static final String DICTIONARY = UPPERCASE + LOWERCASE + NUMBER + SPECIAL;

    public String generatePassword(int length){
        String password = "";
        char oldChar = '\0';
        char newChar = '\0';

        for (int i = 0; i < length; i++){
            int index = random.nextInt(DICTIONARY.length());
            newChar = DICTIONARY.charAt(index);
            if ((i == 0) || (newChar != oldChar)) {
                password += newChar;
                oldChar = newChar;
            } else {
                i--;
            }
        }

        return password;
    }
}
