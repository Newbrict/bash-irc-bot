1) To build the package

   `mvn clean package`

This will generate an executable `jar` file under `./target/`

2) To run the executable 

   `java -jar eliza-bot.jar [<path to custom script>]`

   (Note: If you do not specify a new script, the default one, which is packaged into the `jar` will be used)