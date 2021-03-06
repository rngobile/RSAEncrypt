package oracle.wallet.maintenance;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Random;
import java.util.Arrays;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.KeyFactory; 
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;

public class RSAEncryptJDK6 {
   
    public static void generateKey(){
        try {
            KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
            final int keySize = 2048;
            keygen.initialize(keySize);      

            KeyPair keypair = keygen.genKeyPair();

            PublicKey publicKey = keypair.getPublic();
            PrivateKey privateKey = keypair.getPrivate();

            Random random = new Random();
            Integer randomNum = random.nextInt(2147483647) + 1;
            String outFile = randomNum.toString();

            FileOutputStream outKey = new FileOutputStream(outFile + ".key");
            outKey.write(privateKey.getEncoded());
            outKey.close();

            FileOutputStream outPub = new FileOutputStream(outFile + ".pub");
            outPub.write(publicKey.getEncoded());
            outPub.close();

            System.out.println("Private Key(" + privateKey.getFormat() + "): " + outFile + ".key");
            System.out.println("Public Key(" + publicKey.getFormat() + "): " + outFile + ".pub");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private static PrivateKey getPrivate(String privateFile) throws Exception {
        File file = new File(privateFile);
        FileInputStream inPrivate = new FileInputStream(file);

        byte[] bytes = new byte[(int) file.length()];
        inPrivate.read(bytes);
        
        PKCS8EncodedKeySpec keyspec = new PKCS8EncodedKeySpec(bytes);
        KeyFactory keyfactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyfactory.generatePrivate(keyspec);

        return privateKey;
    }

    private static PublicKey getPublic(String publicFile) throws Exception {
        File file = new File(publicFile);
        FileInputStream inPublic = new FileInputStream(file);

        byte[] bytes = new byte[(int) file.length()];
        inPublic.read(bytes);
        
        X509EncodedKeySpec keyspec = new X509EncodedKeySpec(bytes);
        KeyFactory keyfactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyfactory.generatePublic(keyspec);

        return publicKey;
    }

    public static void encrypt2File(String publicFile, String file) throws Exception {
        byte[] bytes = new byte[file.length()];
        int i; 
        PublicKey publicKey = getPublic(publicFile);
        Cipher cipher = Cipher.getInstance("RSA");  
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);  

        FileInputStream fileIn = new FileInputStream(file);
        FileOutputStream fileOut = new FileOutputStream(file + ".enc");

        CipherOutputStream cipherOut = new CipherOutputStream(fileOut, cipher);

        while (( i = fileIn.read(bytes)) != -1) {
            cipherOut.write(bytes,0,i);
        }

        cipherOut.close();
    }
    
    public static String encryptB64(String publicFile, String file, int maxFile) throws Exception {
        B64 base64 = new B64();
        return base64.encode(encrypt(publicFile, file), maxFile);
    }

    public static byte[] encrypt(String publicFile, String message) throws Exception {
        PublicKey publicKey = getPublic(publicFile);
        Cipher cipher = Cipher.getInstance("RSA");  
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);  

        return cipher.doFinal(message.getBytes());
    }

    public static String decrypt(String privateFile, byte[] message) throws Exception {
        PrivateKey privateKey = getPrivate(privateFile);
        Cipher cipher = Cipher.getInstance("RSA");  
        cipher.init(Cipher.DECRYPT_MODE, privateKey);  
        
        String decryptedMessage = new String(cipher.doFinal(message));
        return decryptedMessage;
    }

    public static String decryptFromB64(String privateFile, String message, int maxFile) throws Exception{
        B64 base64 = new B64();
        return decrypt(privateFile, base64.decode(message.getBytes(), maxFile));
    }
    
    public static String decryptFromFile(String privateFile, String file) throws Exception {
        byte[] bytes = new byte[file.length()];
        int i;

        PrivateKey privateKey = getPrivate(privateFile);
        Cipher cipher = Cipher.getInstance("RSA");  
        cipher.init(Cipher.DECRYPT_MODE, privateKey);  

        FileInputStream fileIn = new FileInputStream(file);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        while (( i = fileIn.read(bytes)) != -1) {
            buffer.write(bytes,0,i);
        }
        buffer.flush();

        /*
        FileOutputStream fileOut = new FileOutputStream(file + ".dec");

        CipherOutputStream cipherOut = new CipherOutputStream(fileOut, cipher);

        while (( i = fileIn.read(bytes)) != -1) {
            cipherOut.write(bytes,0,i);
        }

        cipherOut.close();
        */

        String decrypted = new String(cipher.doFinal(buffer.toByteArray()));
        
        return decrypted;
    }

}
