#! /bin/sh

echo $3 >> input
cat input | java -jar ./eliza_java.jar > output

echo "PRIVMSG $2 :$1: `tail -n 1 output`"
