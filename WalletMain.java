import oracle.wallet.maintenance.*;
import org.apache.commons.cli.*;
import java.util.List;
import java.util.ArrayList;

public class WalletMain {
    static HelpFormatter formatter = new HelpFormatter();

    public static void printHelp(Options options){
        formatter.printHelp("WalletMain [--help | [-g] | [-e <FILE> -p <PUBLIC KEY>] | [-w -c <CONFIG FILE> | -f <alias1,alias2,etc..> | -t]", options);
        System.exit(0);
    }

    public static void changePasswords(WalletInfo entry, ManageWallet wallet, PropertyFile config, Boolean testFlag) throws Exception{
        String tnsAdmin = config.getProperty("tnsAdmin");
        String walletLocation = config.getProperty("walletLocation");
        int id = entry.getId();
        String alias = entry.getAlias();

        OracleDB db = new OracleDB(tnsAdmin, walletLocation);
        db.connect(alias);
        if (testFlag) {
            db.test("sysdate");
        } else {
            PasswordGenerator gen = new PasswordGenerator();
            String newPassword = gen.generatePassword(30);
            boolean success = db.changePassword(entry.getName(), newPassword, entry.getPassword());
            if (success){
                wallet.changePassword(id, newPassword);
            } else{
                System.out.println("!! -- Skipping " + alias + ": Please fix manually with the --fix-database argument. --!!" );
    
            }
       }
    }

    public static void main(String[] args){

        CommandLineParser parser = new BasicParser();
        Options options = new Options();
        CommandLine cmd = null;

        options.addOption(OptionBuilder.withArgName("e")
                .withLongOpt("encrypt-file")
                .hasArgs()
                .withArgName("file")
                .withDescription("encrypt file")
                .create("e")
                );
        options.addOption(OptionBuilder.withArgName("p")
                .withLongOpt("public-key")
                .hasArgs()
                .withArgName("public-key")
                .withDescription("public key file")
                .create("p")
                );
        options.addOption(OptionBuilder.withArgName("w")
                .withLongOpt("wallet")
                .withDescription("change passwords for wallet entries")
                .create("w")
                );
        options.addOption(OptionBuilder.withArgName("c")
                .withLongOpt("config-file")
                .hasArgs()
                .withArgName("config-file")
                .withDescription("load config file")
                .create("c")
                );
        options.addOption(OptionBuilder.withArgName("f")
                .withLongOpt("fix-database")
                .hasArgs()
                .withArgName("database-aliases")
                .withDescription("fix one off wallet entries, use tns alias for wallet with comma separation, no space")
                .create("f")
                );
        options.addOption("t", "test-database", false, "test database connections from the wallet");
        options.addOption("h", "help", false, "shows help");
        options.addOption("g", "generate-key", false, "create key pair");

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e){
            System.out.println("Failed to parse command line: " + e);
            printHelp(options);
        }

        if (cmd.hasOption("h")){
            printHelp(options);
        } else if (cmd.hasOption("g")){
            RSAEncryptJDK6 gen = new RSAEncryptJDK6();
            gen.generateKey();
        } else if (cmd.hasOption("e")){
            if(cmd.hasOption("p")){
                String file = cmd.getOptionValue("e");
                String publicKey = cmd.getOptionValue("p");

                try {
                   String encrypted = RSAEncryptJDK6.encryptB64(publicKey, file);
                   System.out.println(encrypted);
                } catch (Exception e){
                   e.printStackTrace(); 
                }
            } else {
                System.out.println("ERROR: Please provide public key.");
                printHelp(options);
            }
        } else {
            if (cmd.hasOption("c")){
                String file = cmd.getOptionValue("c");
                PropertyFile config = new PropertyFile(file);
                Boolean testFlag;
                if (cmd.hasOption("t")){
                    testFlag = true;
                } else {
                    testFlag = false;
                }

                if (cmd.hasOption("w")) {
                    String walletLocation = config.getProperty("walletLocation");
                    int maxFiles = Integer.parseInt(config.getProperty("maxFiles"));
                    String secretKey = config.getProperty("secretKey");
                    String message = config.getProperty("message");

                    RSAEncryptJDK6 rsa = new RSAEncryptJDK6();
                    try{
                        String newMessage = rsa.decrypt(secretKey, message);
                        ManageWallet wallet = new ManageWallet(walletLocation, newMessage);
                        List<WalletInfo> entries = wallet.listWallet();

                        if (cmd.hasOption("f")) {
                            String[] fixEntries = cmd.getOptionValue("f").toLowerCase().split(",");
                            for (int j = 0; j < fixEntries.length; j++){
                                for (int k = 0; k < entries.size(); k++){
                                    if (fixEntries[j].equals(entries.get(k).getAlias())){
                                        changePasswords(entries.get(k), wallet, config, testFlag);
                                    }
                                }
                            }
                        } else {
                            for (int i = 0; i < entries.size(); i++){
                                changePasswords(entries.get(i), wallet, config, testFlag);
                           }
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                        System.exit(0);
                    }

                }
            } else {
                System.out.println("ERROR: Please provide config file.");
                printHelp(options);
            }

       }
    }
}
