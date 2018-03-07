import java.io.*;
import java.security.*;
import java.util.Random;
import java.nio.file.*;
import java.security.spec.*;
import javax.crypto.*;

public class RSAEncrypt {
    private static void generateKey() throws IOException, NoSuchAlgorithmException, NoSuchProviderException{
        KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
        keygen.initialize(2048);
        KeyPair keypair = keygen.genKeyPair();
        Key privateKey = keypair.getPrivate();
        Key publicKey = keypair.getPublic();
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
    }

    private static PrivateKey loadPrivate(String privateFile) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException{
        Path path = Paths.get(privateFile);
        byte[] bytes = Files.readAllBytes(path);
        PKCS8EncodedKeySpec keyspec = new PKCS8EncodedKeySpec(bytes);
        KeyFactory keyfactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyfactory.generatePrivate(keyspec);
        return privateKey;
    }

    private static PublicKey loadPublic(String publicFile) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException{
        Path path = Paths.get(publicFile);
        byte[] keyBytes = Files.readAllBytes(path);
        X509EncodedKeySpec keyspec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyfactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyfactory.generatePublic(keyspec);
        return publicKey;
    }

    private static void processFile(Cipher ci,InputStream in,OutputStream out) throws IllegalBlockSizeException, BadPaddingException, IOException{
        byte[] ibuf = new byte[2024];
        int len;
        while ((len = in.read(ibuf)) != -1) {
            byte[] obuf = ci.update(ibuf, 0, len);
            if ( obuf != null ) out.write(obuf);
        }
        byte[] obuf = ci.doFinal();
        if ( obuf != null ) out.write(obuf);
    }

    private static void encrypt(String publicFile, String inFile) throws Exception {
        PublicKey publicKey = loadPublic(publicFile);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        try(FileInputStream in = new FileInputStream(inFile);
            FileOutputStream out = new FileOutputStream(inFile + ".enc")){
                processFile(cipher, in, out);
            }
    }

    public static void main(String[] args) throws Exception, IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException{
        generateKey();
        PublicKey publicKey = loadPublic("489906619.pub");
        System.out.println(publicKey);
        encrypt("489906619.pub", "hello.txt");
    }
}