package oracle.wallet.maintenance;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
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

    public static void encrypt(String publicFile, String file) throws Exception {
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
    
    public static void decrypt(String privateFile, String file) throws Exception {
        byte[] bytes = new byte[file.length()];
        int i;

        PrivateKey privateKey = getPrivate(privateFile);
        Cipher cipher = Cipher.getInstance("RSA");  
        cipher.init(Cipher.DECRYPT_MODE, privateKey);  

        FileInputStream fileIn = new FileInputStream(file);
        FileOutputStream fileOut = new FileOutputStream(file + ".dec");

        CipherOutputStream cipherOut = new CipherOutputStream(fileOut, cipher);

        while (( i = fileIn.read(bytes)) != -1) {
            cipherOut.write(bytes,0,i);
        }

        cipherOut.close();
    }

}
