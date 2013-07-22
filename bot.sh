#!/bin/bash

. bot.properties
input=".bot.cfg"
echo "NICK $nick" > $input 
echo "USER $user" >> $input
echo "JOIN #$channel" >> $input

tail -f $input | telnet $server 6667 | while read res
do
  # do things when you see output
  case "$res" in
    # respond to ping requests from the server
    PING*)
      echo "$res" | sed "s/I/O/" >> $input 
    ;;
    # for pings on nick/user
    *"You have not"*)
      echo "JOIN #$channel" >> $input
    ;;
    # run when someone joins
    *JOIN*)
      who=$(echo "$res" | perl -pe "s/:(.*)\!.*@.*/\1/")
      if [ "$who" = "$nick" ]
      then
       continue 
      fi
      echo "MODE #$channel +o $who" >> $input
    ;;
    # run when a message is seen
    *PRIVMSG*)
      echo "$res"
      who=$(echo "$res" | perl -pe "s/:(.*)\!.*@.*/\1/")
      from=$(echo "$res" | perl -pe "s/.*PRIVMSG (.*[#]?([a-zA-Z]|\-)*) :.*/\1/")
      # "#" would mean it's a channel
      if [ "$(echo "$from" | grep '#')" ]
      then
        test "$(echo "$res" | grep ":$nick:")" || continue
        will=$(echo "$res" | perl -pe "s/.*:$nick:(.*)/\1/")
      else
        will=$(echo "$res" | perl -pe "s/.*$nick :(.*)/\1/")
        from=$who
      fi
      com=$(echo "$will" | grep -Eio "[a-z]*" | head -n1 | tail -n1)
      if [ -z "$(ls modules/*.sh | grep -i -- "$com.sh")" ]
      then
        ./modules/help.sh $who $from >> $input
        continue
      fi
      ./modules/$com.sh $who $from $(echo "$will" | cut -d " " -f2-99) >> $input
    ;;
    *)
      echo "$res"
    ;;
  esac
done
