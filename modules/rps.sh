#!/bin/bash
# 0 - rock
# 1 - paper
# 2 - scissor

m_choice=$(($RANDOM%3))
o_choice=$(echo $4 | sed -r "s/^[rR].*/0/" | sed -r "s/^[pP].*/1/" | sed -r "s/^[sS].*/2/")

#convert my random to either r/p/s
case "$m_choice" in
  0)
    mr_choice="rock"
  ;;
  1)
    mr_choice="paper"
  ;;
  2)
    mr_choice="scissor"
  ;;
esac

if [ "$m_choice" == "$o_choice" ]
then
  outcome="tie"
elif [ "$((($m_choice+1)%3))" == "$o_choice" ]
then
  outcome="lose"
else
  outcome="win"
fi

echo "PRIVMSG $2 :$1: $4 VS $mr_choice | I $outcome!"
