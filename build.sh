#!/bin/bash

javac -cp lib/*:. WalletMain.java

if [ $? -eq 0 ]; then
    jar cvfm WalletMaintenance.jar manifest WalletMain.class oracle/wallet/maintenance/*.class
else
    exit 0
fi

if [ $? -eq 0 ]; then
    rm -f manifest
    find . -type f -name "*.class" -delete
    find . -type f -name "*.java" -delete
    find . -type d -empty -delete
else 
   exit 0
fi