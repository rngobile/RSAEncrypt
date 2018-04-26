# Oracle Wallet Maintenance

This project was created to maintain Oracle Wallet password on a scheduled time. This will prevent issues with passwords expiring due to profile settings. Passwords are randomly generated with a fixed length of 30 characters.

## Compiling source code
Use the provided `build.sh` script to compile the java source code

```
[oracle@example.com]$ chmod +x build.sh

[oracle@example.com]$ ./build.sh
added manifest
adding: WalletMain.class(in = 6345) (out= 3232)(deflated 49%)
adding: oracle/wallet/maintenance/B64.class(in = 803) (out= 492)(deflated 38%)
adding: oracle/wallet/maintenance/Cipher64.class(in = 1498) (out= 888)(deflated 40%)
adding: oracle/wallet/maintenance/ManageWallet.class(in = 4860) (out= 2593)(deflated 46%)
adding: oracle/wallet/maintenance/OracleDB.class(in = 5152) (out= 2782)(deflated 46%)
adding: oracle/wallet/maintenance/PasswordGenerator.class(in = 1318) (out= 806)(deflated 38%)
adding: oracle/wallet/maintenance/PropertyFile.class(in = 1267) (out= 689)(deflated 45%)
adding: oracle/wallet/maintenance/RSAEncryptJDK6.class(in = 4397) (out= 2142)(deflated 51%)
adding: oracle/wallet/maintenance/SendEmail.class(in = 2318) (out= 1246)(deflated 46%)
adding: oracle/wallet/maintenance/WalletInfo.class(in = 936) (out= 458)(deflated 51%)

[oracle@example.com]$ ls
build.sh  config.properties  lib  README.md  WalletMaintenance.jar
```

## Setting up config.properties

The project will use a config file to determine necessary parameters:

**walletLocation**=*The full path of the wallet folder to be configured*  
**tnsAdmin**=*The location of the Oracle TNS file*  
**maxFiles**=*Required: Max amount of files the program can handle*  
**secretKey**=*The path where the RSA Key is that will be use to decrypt your encoded password file*  
**message**=*This is the password for the oracle wallet*  
**mail.to**=*Mail To line, email addresses seperated by commas, no space*  
**mail.from**=*Mail From line, single email address*  
**mail.host**=*Mail host server*  
**mail.cc**=*Mail CC line, email addresses seperated by commas*  

Example config.properties:
```
walletLocation = /home/oracle/wallets
tnsAdmin = /u01/app/oracle/product/12.1.0/dbhome_2/network/admin/
maxFiles = 300
secretKey = /u01/app/oracle/keystore/1518263116.key
message = secretpassword
mail.to = example@example.com
mail.from = example@example.com
mail.host = smtp.gmail.com
mail.cc=
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

Place your the path to your Private Key that was generated in your config.properties file in the `secretKey` field.  
**Note:** For better security, place this key outside of where the code resides.

### Encrypt your password in the config.properties file by running the below command
Usage:
```
java -jar WalletMaintenance.jar -e <CONFIG_FILE> -p <PUBLIC_KEY>
```
Example Output:
```
[oracle@example.com]$ java -jar WalletMaintenance.jar -e config.properties -p 1518263116.pub

[oracle@example.com]$ cat config.properties
#Wed Apr 25 19:01:23 PDT 2018
secretKey=I0RtJP6e`E>s_0GeV/uiI/qibUK-_0GiIwB/JwR1JwRvKQdr^/S2
walletLocation=/home/oracle/wallets
mail.to=example@example.com
mail.from=example@example.com
mail.host=smtp.gmail.com
maxFiles=999
mail.cc=
tnsAdmin=/u01/app/oracle/product/12.1.0/dbhome_2/network/admin/
message=^Aqc,_iRfQws0hjQD1FIt,HLEu_d52JUTSsfVNohQ.KPJ1CF5UEmCIT>ObBDs-25srctlW2qi`V(Iv`aP16sp.aVCVT(MOM>vO,-UlEu0eK..ehw.A.,LAS3-_SN-JLuqeRe1OFihW,^bAbLhaneATirAa.f-2/BAP(Oj3Ns(0/arQ3JqLSUQW>tTRvEkKNB@FVuOR/16LD>owWNLQJTA6Kb/H1Dg/-qDJk5OQVhaIUhfjFamC0o3ip0h3R0O.Pwbd/s6c5w.p.@LDm._Ub060C4U`125^4Gr?0.O@bHMG/VI1qQemkMrNOCBpsS3aPRMuSJOS4j40OhAhF`H6whCN\=\=
```

### Running the password change

Usage:
```
java -jar WalletMaintenance.jar -w -c <CONFIG_FILE>
```

Example Output:
```
[oracle@example.com]$ java -jar WalletMaintenance.jar -w -c config.properties
Connecting to wallet1.com..
Success: wallet1 password has been changed.
Success: wallet password has been changed for oracle.security.client.password3.
Connecting to wallet.manager..
Success: walletmanager password has been changed.
Success: wallet password has been changed for oracle.security.client.password2.
Connecting to eggplant.test..
Success: rn_test password has been changed.
Success: wallet password has been changed for oracle.security.client.password1.
```

### Testing Database Connections from the Wallet

Alternatively, you may test if the connection to the database before or after running the program:

Usage:
```
[oracle@example.com]$ java -jar WalletMaintenance.jar -w -c <CONFIG_FILE> -t
```

Example:
```
[oracle@example.com]$ java -jar WalletMaintenance.jar -w -c config.properties -t
Connecting to wallet1.example..
2018-04-01 17:53:31.0
Connecting to wallet2.example..
2018-04-01 17:53:31.0
Connecting to wallet3.example..
2018-04-01 17:53:31.0
```

### If there was a connection issue during the password change, you can run --fix-database

Usage:
```
java -jar WalletMaintenance.jar -w -c <CONFIG FILE> --fix-database <alias1,alias2,etc..>
```

Example Output:
```
[oracle@example.com]$ mkstore -listCredential -wrl /home/oracle/wallets
Oracle Secret Store Tool : Version 12.1.0.2
Copyright (c) 2004, 2014, Oracle and/or its affiliates. All rights reserved.

Enter wallet password:
List credential (index: connect_string username)
3: WALLET1.COM WALLET1
2: wallet.manager walletmanager
1: eggplant.test rn_test

[oracle@example.com]$ java -jar WalletMaintenance.jar -w -c config.properties --fix-database wallet1.com,eggplant.test
Connecting to wallet1.com..
Success: wallet1 password has been changed.
Success: wallet password has been changed for oracle.security.client.password3.
Connecting to eggplant.test..
Success: rn_test password has been changed.
Success: wallet password has been changed for oracle.security.client.password1.
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
+ ~~Email Reporting Service for Errors~~
+ ~~Base64 Class~~
+ Command-Line Argument to Regenerate Everything including the Wallet itself.
+ ~~SetProperty for ConfigFile~~
+ ~~Encrypt secretKey and message parameters~~
+ Threading

