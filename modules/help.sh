echo "PRIVMSG $2 :$1: The available modules I have are:"
output=$(find modules/ -maxdepth 1 -type f -not -name ".*" -printf "%f\n" | sed "s/.sh//")
echo "PRIVMSG $2 :$1: "$output
