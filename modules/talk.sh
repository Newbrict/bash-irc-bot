#! /bin/sh

arg1=$1
arg2=$2

shift 2 
echo "$@" >> input
cat input | java -jar ./eliza_java.jar > output

echo "PRIVMSG $arg2 :$arg1: `tail -n 1 output`"
