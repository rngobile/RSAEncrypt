package oracle.wallet.maintenance;

import java.io.*;
import java.util.List;
import java.util.ArrayList;

public class ManageWallet {
    private String walletLocation, walletPassword;
    private Process process = null;
    private Runtime runtime = null;

    public ManageWallet(String walletLocation, String walletPassword){
        this.walletLocation = walletLocation;
        this.walletPassword = walletPassword;

        this.runtime = Runtime.getRuntime();
    }
    
    public List<String> listWallet() throws Exception{
       String cmd = "mkstore -wrl " + this.walletLocation + " -listCredential -nologo";
       String line = "";
       List<String> entries = new ArrayList<String>();
       List<String> error = new ArrayList<String>();

       process = this.runtime.exec(cmd);
       OutputStream passwordIn = process.getOutputStream();
       passwordIn.write((this.walletPassword + "\n").getBytes());
       passwordIn.flush();

       BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
       while (( line = input.readLine()) != null ){
           if ( line.matches("(\\d)+:(.*)") ) { 
               entries.add(line);
           } else {
               error.add(line);
           }
       }

       if (entries.size() > 0 ){
           return entries;
       } else {
           return error;
       }
    }
}
