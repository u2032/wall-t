#!/bin/sh

cd $(dirname "$0")

JAVA=$JAVA_HOME
if [ -z "$JAVA" ]
then
        echo "Java path is not configured."
        echo "Please define JAVA_HOME environement variable or edit Java path into this script."
        exit
fi


JAVA_VER=$($JAVA/bin/java -version 2>&1 | sed 's/java version "\(.*\)\.\(.*\)\..*"/\1\2/; 1q')
if [ "$JAVA_VER" -ge 18 ] 
then
        echo "Checking Java version: It's Ok." 
else 
        echo "This program requires Java version greater than 1.8."
        echo "Please define JAVA_HOME environement variable or edit Java path into this script."
        exit
fi

cd ./bin
$JAVA/bin/java -jar ./bin/Wall-T.jar