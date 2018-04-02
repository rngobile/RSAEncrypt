package oracle.wallet.maintenance;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class ManageWallet {
    private String walletLocation, walletPassword;
    private Process process = null;
    private Runtime runtime = null;

    public ManageWallet(String walletLocation, String walletPassword){
        Map<String,String> variables = new HashMap<String,String>();
        variables.put("walletLocation",walletLocation);
        checkInjections(variables);

        this.walletLocation = walletLocation;
        this.walletPassword = walletPassword;

        this.runtime = Runtime.getRuntime();
    }

    private BufferedReader executeCommand(String cmd) throws Exception{
        process = this.runtime.exec(cmd);
        OutputStream passwordIn = process.getOutputStream();
        passwordIn.write((this.walletPassword + "\n").getBytes());
        passwordIn.flush();

        BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
        return input;
    }

    private void checkInjections(Map<String, String> variables){
        char[] injection = {';','|','$','`','&'};

        for (int i = 0; i < injection.length; i++){
            for(Map.Entry<String,String> entry: variables.entrySet()){
                if(entry.getValue().indexOf(injection[i]) >= 0){
                    System.out.println("Error: Character " + injection[i] + " is not allowed for " + entry.getKey());
                    return;
                }
            }
        }
    }
    
    public List<WalletInfo> listWallet() throws Exception{
       String cmd = "mkstore -wrl " + this.walletLocation + " -listCredential -nologo";
       String line = "";
       List<WalletInfo> entries = new ArrayList<WalletInfo>();
       List<String> error = new ArrayList<String>();
       int id;
       String alias;
       String username;

       BufferedReader input = executeCommand(cmd);
       while (( line = input.readLine()) != null ){
           if ( line.matches("(\\d)+:(.*)") ) { 
               id =  Integer.parseInt(line.split(":")[0]);
               alias = line.split(":")[1].trim().split(" ")[0].toLowerCase();
               username = line.split(":")[1].trim().split(" ")[1].toLowerCase();

               WalletInfo entry = new WalletInfo(id, alias, username);
               entries.add(entry);
           } else {
               error.add(line);
           }
       }

       if (entries.size() <= 0 ){
           for(int i = 0; i < error.size(); i++){
               System.out.println(error.get(i));
           }
           System.exit(0);
       } else {
           for(int i = 0; i < entries.size(); i++){
               id = entries.get(i).getId();
               entries.get(i).setPassword(getPassword(id));
           }
       } 

       return entries;
    }

    private String getPassword(int id) throws Exception{
        String entryAlias = "oracle.security.client.password" + id;
        String cmd = "mkstore -wrl " + this.walletLocation + " -viewEntry " + entryAlias + " -nologo";
        BufferedReader input = executeCommand(cmd);
        String line, password = "";

        while (( line = input.readLine()) != null){
            if (line.matches(entryAlias + "(.*)")){
                password = line.split("=",2)[1].trim();
            } 
        }

        if (password == null){
            System.out.println("ERROR: alias " + entryAlias + " does not exist.");
        }

        return password;
    }

    public void changePassword(int id, String password) throws Exception{
        String entryAlias = "oracle.security.client.password" + id;
        String cmd = "mkstore -wrl " + this.walletLocation + " -modifyEntry " + entryAlias + " " + password + " -nologo";
        BufferedReader input =  executeCommand(cmd);
        String line = "";
        List<String> lines = new ArrayList<String>();

        while (( line = input.readLine()) != null){
            lines.add(line); 
        }

        if (lines.size() > 1){
            System.out.println("ERROR: wallet password change failed.");
            for (int i = 1; i < lines.size(); i++){
                System.out.println(lines.get(i));
            }
        } else {
            System.out.println("Success: wallet password has been changed for " + entryAlias + ".");
        }
    }
}
