#! /bin/sh


arg1=$1 # sender (the person I am talking to)
arg2=$2 # unclear????

user_dir=eliza_bot/$1
if [ ! -d "$user_dir" ]; then
    mkdir $user_dir
fi

shift 2  # chop off the first two args
echo "$@" >> $user_dir/input
cat $user_dir/input | java -jar eliza_bot/eliza_java.jar > $user_dir/output

# retcode of value 1 means the user said goodbye
retcode="${PIPESTATUS[1]}"

echo "PRIVMSG $arg2 :$arg1: `tail -n 1 $user_dir/output`"

if [ $retcode -eq "1" ]; then
    echo "PRIVMSG $arg2 :$arg1: That was a joke! It's free to talk to me. But we do accept donations! ;D"
fi