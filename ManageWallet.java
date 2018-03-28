import java.io.*;

public class ManageWallet {
    private String walletLocation, walletPassword;
    private Process process = null;
    private Runtime runtime = null;

    public ManageWallet(String walletLocation, String walletPassword){
        this.walletLocation = walletLocation;
        this.walletPassword = walletPassword;

        this.runtime = Runtime.getRuntime();
    }
    
    public void listWallet() throws Exception{
       String cmd = "mkstore -wrl " + this.walletLocation + " -listCredential -nologo";
       String line = "";
       process = this.runtime.exec(cmd);
       OutputStream passwordIn = process.getOutputStream();
       passwordIn.write((this.walletPassword + "\n").getBytes());
       passwordIn.flush();

       BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
       while (( line = input.readLine()) != null ){
           System.out.println(line);
       }

    }
}
