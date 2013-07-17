# !/bin/bash

. bot.properties
config=".bot.cfg"
echo "NICK $nick" > $config 
echo "USER $user" >> $config
echo "JOIN #$channel" >> $config

tail -f $config | telnet $server 6667 | while read res;
do
  # do things when you see output
  case "$res" in
    PING*)
      echo "$res" | sed "s/PING/PONG/" >> $config 
    ;;
    # for pings on nick/user
    *"You have not"*)
      echo "JOIN #$channel" >> $config
    ;;
    *JOIN*)
      whojoin=$(echo "$res" | sed -r "s/:(.*)\!.*@.*/\1/")
      echo "PRIVMSG #$channel :Welcome $whojoin :)" >> $config
    ;;
    *)
      echo "$res"
    ;;
  esac
done
