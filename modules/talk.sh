#! /bin/sh
#  Simple Question-Answering module
#  author Vy Nguyen
#

arg1=$1 # sender (the person I am talking to)
arg2=$2 # unclear????

user_dir=modules/talk/logs/$1
if [ ! -d "$user_dir" ]; then
    mkdir $user_dir
fi

# if the user said goodbye and changed her mind!
if [ -f "$user_dir/GOODBYE" ]; then
    echo "PRIVMSG $arg2 :$arg1: I thought you just said goodbye!"
    rm $user_dir/GOODBYE
fi

shift 3  # chop off the first two args
echo "$@" >> $user_dir/input
cat $user_dir/input | java -jar modules/talk/talk_bot.jar > $user_dir/output

# retcode of value 1 means the user said goodbye
retcode="${PIPESTATUS[1]}"

echo "PRIVMSG $arg2 :$arg1: `tail -n 1 $user_dir/output`"

if [ $retcode -eq "1" ]; then
    echo "PRIVMSG $arg2 :$arg1: That was a joke! It's free to talk to me. But we do accept donations! ;D"
    # clean up old conversations
    stamp="`date +%s`"
    mv $user_dir/input $user_dir/"input_$stamp"
    mv $user_dir/output $user_dir/"output_$stamp"
    # leave good-bye flag (indicating the user already said goodbye)
    touch $user_dir/GOODBYE
fi
