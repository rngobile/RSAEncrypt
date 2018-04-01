import oracle.wallet.maintenance.*;
import org.apache.commons.cli.*;

public class WalletMain {
    static HelpFormatter formatter = new HelpFormatter();

    public static void printHelp(Options options){
        formatter.printHelp("WalletMain [--help] | [--config-file <CONFIG FILE>] | [--generate-key]", options);
        System.exit(0);
    }

    public static void main(String[] args){

        CommandLineParser parser = new BasicParser();
        Options options = new Options();
        CommandLine cmd = null;

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
        } else if ( cmd.hasOption("c")){
            String file = cmd.getOptionValue("c");
            PropertyFile config = new PropertyFile(file);
            String walletLocation = config.getProperty("walletLocation");
            System.out.println(walletLocation);
        }
    }
}
