import oracle.wallet.maintenance.*;
import org.apache.commons.cli.*;

public class WalletMain {
    static HelpFormatter formatter = new HelpFormatter();

    public static void printHelp(Options options){
        formatter.printHelp("WalletMain [--help | [-g] | [-w -c <CONFIG FILE>]", options);
        System.exit(0);
    }

    public static void main(String[] args){

        CommandLineParser parser = new BasicParser();
        Options options = new Options();
        CommandLine cmd = null;

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
                    String fixEntries = config.getProperty("fixEntries");

                    RSAEncryptJDK6 rsa = new RSAEncryptJDK6();
                    String newMessage = rsa.decrypt(secretKey, message);

                    ManageWallet wallet = new ManageWallet(walletLocation, newMessage);
                    try{
                        List<String> entries = wallet.listWallet();

                        for(int i = 0; i < entries.size(); i++){
                            System.out.println(entries.get(i));
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
