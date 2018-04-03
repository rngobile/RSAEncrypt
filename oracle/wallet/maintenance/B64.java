package oracle.wallet.maintenance;

import org.apache.commons.codec.binary.Base64;

public class B64{
    private Base64 base64 = null;

    public B64(){
        this.base64 = new Base64();
    }
    
    public String encode(byte[] bytes){
        return new String(this.base64.encode(bytes));
    }

    public String decode(byte[] bytes){
        return new String(this.base64.decode(bytes));
    }
}