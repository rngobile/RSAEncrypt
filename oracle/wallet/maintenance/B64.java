package oracle.wallet.maintenance;

import org.apache.commons.codec.binary.Base64;

public class B64{
    private Base64 base64 = null;

    public B64(){
        this.base64 = new Base64();
    }
    
    public String encode(byte[] bytes, int rot){
        String message64 = new String(this.base64.encode(bytes));
        Cipher64 cipher = new Cipher64(message64, rot);
        return cipher.encrypt();
    }

    public byte[] decode(byte[] bytes, int rot){
        String message64 = new String(bytes);
        Cipher64 cipher = new Cipher64(message64, rot);
        return this.base64.decode(cipher.decrypt().getBytes());
    }
}