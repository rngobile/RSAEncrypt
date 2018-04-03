import oracle.wallet.maintenance.B64;

public class Test {
    public static void main(String[] args){
        String string = "oracle2000";
        B64 base64 = new B64();

        String encoded = new String(base64.encode(string.getBytes()));
        String decoded = new String(base64.decode(encoded.getBytes()));

        System.out.println(encoded);
        System.out.println(decoded);
    }
    
}
