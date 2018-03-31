package oracle.wallet.maintenance;

import java.sql.*;

class OracleDB{
    static final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";
    private String tnsAdmin = "/u01/app/oracle/product/12.1.0/dbhome_2/network/admin";
    private String walletLocation = "/home/oracle/wallets";
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

    public void connect(String tnsName){
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

    public void changePassword(String user, String newPassword, String oldPassword){
        if (this.conn != null) {
            try {
                char[] injection = {'"','\'',';','-'};
                String sql = "alter user " + user + " identified by \"" + newPassword + "\" replace \"" + oldPassword  + "\"";

                for (int i = 0; i < injection.length; i++){
                    if (user.indexOf(injection[i]) >= 0){
                        System.out.println("Error: Character " + injection[i] + " is not allowed for user.");
                        return;
                    } else if (newPassword.indexOf(injection[i]) >= 0 ){
                        System.out.println("Error: Character " + injection[i] + " is not allowed for newPassword.");
                        return;
                    } else if (oldPassword.indexOf(injection[i]) >= 0 ){
                        System.out.println("Error: Character " + injection[i] + " is not allowed for oldPassword.");
                        return;
                    }
                }

                Statement stmt = conn.createStatement();
                stmt.setEscapeProcessing(false);
                stmt.executeUpdate(sql);
                System.out.println("Success: " + user + " password has been changed.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else{
            System.out.println("Failed to make connection!");
        }
    }
}
