package oracle.wallet.maintenance;

public class WalletMain {
    public static void main(String[] args){
        String file = args[0];
        PropertyFile config = new PropertyFile(file);
        String walletLocation = config.getProperty("walletLocation");
        System.out.println(walletLocation);
    }
}
