# !/bin/bash
# @author Dimitar Dimitrov

. bot.properties
config=".bot.cfg"
echo "NICK $nick" > $config 
echo "USER $user" >> $config
echo "JOIN #$channel" >> $config

tail -f $config | telnet $server 6667 | while read res
do
  # do things when you see output
  case "$res" in
    # respond to ping requests from the server
    PING*)
      echo "$res" | sed "s/PING/PONG/" >> $config 
    ;;
    # for pings on nick/user
    *"You have not"*)
      echo "JOIN #$channel" >> $config
    ;;
    # run when someone joins
    *JOIN*)
      who=$(echo "$res" | sed -r "s/:(.*)\!.*@.*/\1/")
      if [ "$who" = "$nick" ]
      then
       continue 
      fi
      echo "MODE #$channel +o $who" >> $config
    ;;
    # run when a message is seen
    *PRIVMSG*)
      echo "$res"
      who=$(echo "$res" | sed -r "s/:(.*)\!.*@.*/\1/")
      from=$(echo "$res" | sed -r "s/.*PRIVMSG ([#]?([a-zA-Z]|\-)*) :.*/\1/")
      # "#" would mean it's a channel
      if [ "$(echo "$from" | grep '#')" ]
      then
        test "$(echo "$res" | grep ":$nick:")" || continue
        will=$(echo "$res" | sed -r "s/.*:$nick:(.*)/\1/")
      else
        will=$(echo "$res" | sed -r "s/.*$nick :(.*)/\1/")
        from=$who
      fi
      com=$(echo "$will" | grep -Eio "[a-z]*" | head -n1 | tail -n1)
      if [ -z "$(ls modules/ | grep -i -- "$com.sh")" ]
      then
        ./modules/help.sh $who $from >> $config
        continue
      fi
      ./modules/$com.sh $who $from $(echo "$will" | tail -n+1) >> $config
    ;;
    *)
      echo "$res"
    ;;
  esac
done
