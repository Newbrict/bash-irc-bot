#!/bin/bash

day=$(date +%a)
hour=$(date +%T | sed "s/\([^:]*\):.*/\1/" )
minute=$(date +%M)

percent=0

case $day in
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

percent=$(($percent+$hp+$minute/20))

if [[ "$percent" -ge 100 ]]; then
	percent=99
fi

if [ $# -ge 1 ]; then
	if [ $1 == "azhu" ]; then 
		percent=0;
	fi
	echo "PRIVMSG $2 :$1: There is a $percent% chance azhu is sleeping right now."
else
	echo "There is a $percent% chance azhu is sleeping right now."
fi


