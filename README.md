# Oracle Wallet Maintenance

This project was created to maintain Oracle Wallet password on a scheduled time. This will prevent issues with passwords expiring due to profile settings. Passwords are randomly generated with a fixed length of 30 characters.

## Setting up config.properties

The project will use a config file to determine necessary parameters:

**walletLocation**=*The full path of the wallet folder to be configured*  
**tnsAdmin**=*The location of the Oracle TNS file*  
**maxFiles**=*This is the secret character shift number for encryption/decryption for your message* **--in-development**  
**secretKey**=*The path where the RSA Key is that will be use to decrypt your encoded password file*  
**message**=*The path where the encoded password file that has been encrypted with your public key*  

example config.properties:
```
walletLocation = /home/oracle/wallets
tnsAdmin = /u01/app/oracle/product/12.1.0/dbhome_2/network/admin/
maxFiles = 300
secretKey = 834644167.key
message = hello.txt.enc
```

## Setting up monthly password changes

The `WalletMaintenance.jar` will be the program that will help us automate the password change. Before we can set it up in cron, we will have to generate our public, private key as well with our encoded password file.

commands:
```
usage: WalletMain [--help | [-g] | [-e <FILE> -p <PUBLIC KEY>] | [-w -c <CONFIG FILE> | -f <alias1,alias2,etc..> | -t]
 -f,--fix-database <database-aliases>   fix one off wallet entries, use
                                        tns alias for wallet with comma separation, no space
 -t,--test-database                     test database connections from the
                                        wallet
 -e,--encrypt-file <file>               encrypt file
 -c,--config-file <config-file>         load config file
 -g,--generate-key                      create key pair
 -p,--public-key <public-key>           public key file
 -h,--help                              shows help
 -w,--wallet                            change passwords for wallet
                                        entries
```

### To generate your key pair use the below command:

Usage: 
```
java -jar WalletMaintenance.jar -g
```

Example Output:
```
[oracle@example.com]$ java -jar WalletMaintenance.jar -g
Private Key(PKCS#8): 1518263116.key
Public Key(X.509): 1518263116.pub
```

Place your Private Key that was generated in your config.properties file in the `secretKey` field.

### To create your encoded text, you must feed a file with your password in it on a single line.
Usage:
```
java -jar WalletMaintenance.jar -e <PASSWORD_FILE> -p <PUBLIC_KEY>
```
Example Output:
```
[oracle@example.com]$ echo my_password > example.txt

[oracle@example.com]$ cat example.txt
my_password

[oracle@example.com]$ java -jar WalletMaintenance.jar -e example.txt -p 1518263116.pub

[oracle@example.com]$ ls -lh example.txt.enc
-rw-r--r--. 1 oracle oinstall 256 Apr  1 14:22 example.txt.enc
```

### Running the password change

Usage:
```
java -jar WalletMaintenance.jar -w -c <CONFIG_FILE>
```

Example:
```
java -jar WalletMaintenance.jar -w -c config.properties
```

### Testing Database Connections from the Wallet

Alternatively, you may test if the connection to the database before or after running the program:

Usage:
```
[oracle@example.com]$ java -jar WalletMaintenance.jar -w -t -c <CONFIG_FILE>
```

Example:
```
[oracle@example.com]$ java -jar WalletMaintenance.jar -w -t -c config.properties
Connecting to wallet1.example..
2018-04-01 17:53:31.0
Connecting to wallet2.example..
2018-04-01 17:53:31.0
Connecting to wallet3.example..
2018-04-01 17:53:31.0
```

## Crontab entry:

Modify the crontab to schedule your monthly password change:

crontab time format:
```
* * * * * *
| | | | | | 
| | | | | +-- Year              (range: 1900-3000)
| | | | +---- Day of the Week   (range: 1-7, 1 standing for Monday)
| | | +------ Month of the Year (range: 1-12)
| | +-------- Day of the Month  (range: 1-31)
| +---------- Hour              (range: 0-23)
+------------ Minute            (range: 0-59)
```

Example below for 12:00AM at the first of every month. 
```
00 00 1 * * java -jar /u01/scripts/WalletMaintenance.jar -w -c /u01/config.properties 2>&1 | tee /u01/logs/oracle_wallet_maintenance.log
```


## TO-DO:
+ Email Reporting Service for Errors
+ Base64 Class
+ Command-Line Argument to Regenerate Everything including the Wallet itself.
+ SetProperty for ConfigFile
+ Encrypt secretKey and message parameters
+ Threading

