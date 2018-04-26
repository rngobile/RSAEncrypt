#!/bin/bash

javac -cp lib/*:. WalletMain.javac

if [ $? -eq 0 ]; then
    jar cvfm WalletMaintenance.jar manifest WalletMain.class oracle/wallet/maintenance/*.class
else
    exit 0
fi

if [ $? -eq 0 ]; then
    find . -type f -name "*.class" -exec rm {} \ ;
else 
   exit 0
fi