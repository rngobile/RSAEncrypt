import java.io.*;

public class ManageWallet throws Exception {
    public ManageWallet(String walletLocation, String walletPassword){
        String walletLocation = this.walletLocation;
        String walletPassword = this.walletPassword;

        Runtime runtime = new Runtime.getRuntime();
        Process process = null;
        String line = "";
    }
    
    public void listWallet(String walletLocation){
       String cmd = "mkstore -wrl /home/oracle/wallets -listCredential -nologo";
       process = runtime.exec(cmd);
       OutputStream passwordIn = process.getOutputStream();
       passwordIn.write(password.getBytes());
       passwordIn.flush();

       BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
       while (( line = input.readLine()) != null ){
           System.out.println(line);
       }

    }

}
