#!/bin/bash

. "../../bot.properties"
log="../../$log"

lines=$(grep "!azhu" $log)
name=$(echo $lines | sed "s/.*\]:\(.*\)![^!]*/\1/g")
if [[ "$name" == "" ]]; then
	name="azhu"
fi
line=$(echo $lines | sed "s/.*\[\(.*\)\].*/\1/g")
lastResponse_DayOfMonth=$(echo $line | sed "s/^.*:.*:\(.*\) .*:.*:.*$/\1/g")

dayOfWeek=$(date +%a)
dayOfMonth=$(date +%d)
hour=$(date +%T | sed "s/\([^:]*\):.*/\1/" )
minute=$(date +%M)

daysApart=$((10#$dayOfMonth - 10#$lastResponse_DayOfMonth))
if [ "$daysApart" -eq 0 ]; then
	lastResponse_hour=$(echo $line | sed "s/^.*:.*:.* \(.*\):.*:.*$/\1/g")
	lastResponse_minute=$(echo $line | sed "s/^.*:.*:.* .*:\(.*\):.*$/\1/g")

	minutesBetween=$(((10#$hour - 10#$lastResponse_hour)*60 + 10#$minute - 10#$lastResponse_minute))
fi


responsePercent=0
if [ $minutesBetween -ge 0	 ]; then
	responsePercent=$(( -5000/($minutesBetween+40) - $minutesBetween/25 + 100 ))
fi

percent=0

case $dayOfWeek in
	"Mon" )
		percent=$(($percent+25));;
	"Tue" )
		percent=$(($percent+20));;
	"Wed" )
		percent=$(($percent+20));;
	"Thu" )
		percent=$(($percent+25));;
	"Fri" )
		percent=$(($percent+30));;
	"Sat" )
		percent=$(($percent+100));;
	"Sun" )
		percent=$(($percent+100));;
esac

hp=$((10#$hour*10#$hour*10#$hour/150))

percent=$((10#$percent+10#$hp+10#$minute/20))
percent=$(($percent + $responsePercent))


if [ "$percent" -le 0 ]; then
	percent=0
elif [ "$percent" -ge 100 ]; then
	percent=99
fi

if [ $# -ge 1 ]; then
	if [ $1 == "azhu" ]; then 
		percent=0;
	fi
	echo "PRIVMSG $2 :$1: There is a $percent% chance $name is sleeping right now."
else
	echo "There is a $percent% chance $name is sleeping right now."
fi


