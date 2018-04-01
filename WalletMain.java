import oracle.wallet.maintenance.*;
import org.apache.commons.cli.*;
import java.util.List;
import java.util.ArrayList;

public class WalletMain {
    static HelpFormatter formatter = new HelpFormatter();

    public static void printHelp(Options options){
        formatter.printHelp("WalletMain [--help | [-g] | [-e <FILE> -p <PUBLIC KEY>] | [-w -c <CONFIG FILE>]", options);
        System.exit(0);
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
                   RSAEncryptJDK6.encrypt(publicKey, file);
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

                if (cmd.hasOption("w")) {
                    String walletLocation = config.getProperty("walletLocation");
                    String tnsAdmin = config.getProperty("tnsAdmin");
                    int maxFiles = Integer.parseInt(config.getProperty("maxFiles"));
                    String secretKey = config.getProperty("secretKey");
                    String message = config.getProperty("message");
                    String[] fixEntries = config.getProperty("fixEntries").split(",");

                    RSAEncryptJDK6 rsa = new RSAEncryptJDK6();
                    try{
                        String newMessage = rsa.decrypt(secretKey, message);
                        ManageWallet wallet = new ManageWallet(walletLocation, newMessage);
                        List<WalletInfo> entries = wallet.listWallet();

                        if ((fixEntries == null) && (fixEntries.length <= 0)) {
                            for (int i = 0; i < entries.size(); i++){
                                OracleDB db = new OracleDB(tnsAdmin, walletLocation);
                                db.connect(entries.get(i).getName());
                                db.test("richard");
                            }
                        } else {
                            for (int j = 0; j < fixEntries.length; j++){
                                for (int k = 0; k < entries.size(); k++){
                                    if (fixEntries[j].equals(entries.get(k).getName())){
                                        OracleDB db = new OracleDB(tnsAdmin, walletLocation);
                                        db.connect(entries.get(k).getName());
                                        db.test("richard");
                                    }
                                }
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
