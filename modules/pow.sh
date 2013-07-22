#! /bin/sh
#  Ted Meyer
#  Incredibly stupid Math bot
#
sender=$1 # sender (the person I am talking to)
channel=$2 # channel
equ=$3 # math equ
res=$(java -cp math/ nums $equ)
echo "PRIVMSG $channel :$sender: $res"