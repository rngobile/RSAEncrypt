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
            System.out.println("Write SQL Statement here");
        } else{
            System.out.println("Failed to make connection!");
        }

    }
}