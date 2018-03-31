import java.sql.*;

class OracleDB{
    static final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";
    public static void main(String[] args) throws Exception{
        System.setProperty("oracle.net.tns_admin","/u01/app/oracle/product/12.1.0/dbhome_2/network/admin");
        System.setProperty("oracle.net.wallet_location","/home/oracle/wallets");

        try{
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            System.out.println("Oracle JDBC Driver not found.");
            e.printStackTrace();
            return;
        }

        Connection connection = null;

        try {
            connection = DriverManager.getConnection("jdbc:oracle:thin:/@eggplant.test");
        } catch (SQLException e) {
            System.out.println("Connection Failed!");
            e.printStackTrace();
            return;
        }


        if (connection != null) {
            try {
                String user = "rn_test";
                String newPassword = "password";
                String oldPassword = "rn_test";
                String sql = "alter user " + user + " identified by \"" + newPassword + "\" replace \"" + oldPassword  + "\"";
                char[] injection = {'"','\'',';','-'};
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

                Statement stmt = connection.createStatement();
                stmt.setEscapeProcessing(false);
                stmt.executeUpdate(sql);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else{
            System.out.println("Failed to make connection!");
        }

    }
}