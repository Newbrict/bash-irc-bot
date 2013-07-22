echo "PRIVMSG $2 :$1: The available modules I have are:"
output=$(ls modules/)
echo "PRIVMSG $2 :$1: "$output
