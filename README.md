bash-irc-bot
============

A simple, modular IRC bot written in bash

any .sh script in modules/ will be executed with all arguments when called like so:

botnickname: test optional

will run test.sh with the arguments
$1 = your nick
$2 = from where it came (room/channel)
$3-X the rest of the args
