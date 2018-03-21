import oracle.wallet.maintenance.RSAEncryptJDK6;
import org.apache.commons.cli.*;

public class Test {

    public static void main(String[] args){
        RSAEncryptJDK6 test = new RSAEncryptJDK6();
        Options options = new Options();
        Options decrypt = new Options("help", "print this message");

        //decrypt.addOption("priv","private",true,"Private key.");
        //decrypt.addOption("f","file",true,"Encoded File");


        options.addOption("h","help",false,"show help.");
        options.addOption("g","generate",false,"create key pair.");
        options.addOption(decrypt);
        
        CommandLineParser parser = new BasicParser();
        CommandLine cmd = null;

        HelpFormatter formatter = new HelpFormatter();

        try {
            cmd = parser.parse(options, args);

            if(cmd.hasOption("h")){
                formatter.printHelp("Main", options);
                System.exit(0);
            }

            if(cmd.hasOption("g")){
                try{
                    test.generateKey();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        } catch (ParseException e) {
            System.out.println("Failed to parse command line: " + e );
            formatter.printHelp("Main", options);
            System.exit(0);
        }

        /*
        try{
            test.encrypt("834644167.pub", "hello.txt");
            test.decrypt("834644167.key", "hello.txt.enc");
        } catch (Exception e) {
            System.out.println(e);
        }
        */
    }
}
