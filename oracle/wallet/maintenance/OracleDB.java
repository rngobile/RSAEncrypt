package oracle.wallet.maintenance;

import java.sql.*;
import java.util.Map;
import java.util.HashMap;
import java.io.StringWriter;
import java.io.PrintWriter;

public class OracleDB{
    static final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";
    private String tnsAdmin = "/u01/app/oracle/product/12.1.0/dbhome_2/network/admin";
    private String walletLocation = "/home/oracle/wallets";
    private String configFile = "";
    private Connection conn = null;

    public OracleDB(){
        System.setProperty("oracle.net.tns_admin",tnsAdmin);
        System.setProperty("oracle.net.wallet_location",walletLocation);
    }

    public OracleDB(String tnsAdmin, String walletLocation){
        this.tnsAdmin = tnsAdmin;
        this.walletLocation = walletLocation;
        System.setProperty("oracle.net.tns_admin",this.tnsAdmin);
        System.setProperty("oracle.net.wallet_location",this.walletLocation);
    }

    public OracleDB(String tnsAdmin, String walletLocation, String configFile){
        this.tnsAdmin = tnsAdmin;
        this.walletLocation = walletLocation;
        System.setProperty("oracle.net.tns_admin",this.tnsAdmin);
        System.setProperty("oracle.net.wallet_location",this.walletLocation);
        this.configFile = configFile;
    }

    public void connect(String tnsName){
        System.out.println("Connecting to " + tnsName + "..");
        try{
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            System.out.println("Oracle JDBC Driver not found.");
            e.printStackTrace();
            return;
        }

        try {
            this.conn = DriverManager.getConnection("jdbc:oracle:thin:/@" + tnsName);
        } catch (SQLException e) {
            System.out.println("Connection Failed!");
            e.printStackTrace();
            if ( this.configFile != null && !(this.configFile.isEmpty())){
                SendEmail email = new SendEmail(this.configFile);
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                email.send("[OracleWalletMaintenance] Connection Failed!\n", sw.toString());
            }
            return;
        }
    }

    public void disconnect(){
        try{
            if (this.conn != null) {
                this.conn.close();
            } else {
                System.out.println("ERROR: Not connected.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkInjections(Map<String, String> variables){
        char[] injection = {'"','\'',';','-'};

        for (int i = 0; i < injection.length; i++){
            for(Map.Entry<String,String> entry: variables.entrySet()){
                if(entry.getValue().indexOf(injection[i]) >= 0){
                    System.out.println("Error: Character " + injection[i] + " is not allowed for " + entry.getKey() + ". Value: " + entry.getValue());
                    return;
                }
            }
        }
   }

    public boolean changePassword(String user, String newPassword, String oldPassword){
        if (this.conn != null) {
            try {

                Map<String,String> variables = new HashMap<String,String>();
                variables.put("user",user);
                variables.put("newPassword", newPassword);
                variables.put("oldPassword", oldPassword);
                checkInjections(variables);
                String sql = "alter user " + user + " identified by \"" + newPassword + "\" replace \"" + oldPassword  + "\"";

                Statement stmt = conn.createStatement();
                //stmt.setEscapeProcessing(true);
                stmt.executeUpdate(sql);
                System.out.println("Success: " + user + " password has been changed.");
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                if ( this.configFile != null && !(this.configFile.isEmpty())){
                    SendEmail email = new SendEmail(this.configFile);
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);
                    email.send("[OracleWalletMaintenance] Database Error!\n", sw.toString());
                }
                return false;
            } finally {
                disconnect();
            }
        } else{
            System.out.println("Failed to make connection!");
            return false;
        }
    }

    public void test (String text){
        if (this.conn != null){
            try{
                Map<String,String> variables = new HashMap<String,String>();
                variables.put("text",text);
                checkInjections(variables);
                String sql = "select " + text + " from dual";

                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                while(rs.next()){
                    System.out.println(rs.getString(text));
                }
            } catch (Exception e){
                e.printStackTrace();
            } finally{
                disconnect();
            }
        } else {
            System.out.println("Failed to make connection!");
        }
    }
}
