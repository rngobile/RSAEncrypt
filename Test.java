import oracle.wallet.maintenance.RSAEncryptJDK6;

public class Test {
    public static void main(String[] args){
        RSAEncryptJDK6 test = new RSAEncryptJDK6();
        try{
            test.encrypt("834644167.pub", "hello.txt");
            test.decrypt("834644167.key", "hello.txt.enc");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
