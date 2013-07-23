package eliza;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;


/**
 * Eliza Application.
 */
public class ElizaApp
{
    private static final URL SCRIPT_URL = ElizaApp.class.getClassLoader().getResource("eliza/script");
    public static void main(String args[]) throws IOException
    {
        Eliza eliza = new Eliza(args != null && args.length == 1
                                    ? new FileInputStream(args[0])
                                    : SCRIPT_URL.openStream());
        
        if (eliza.runQuiet(System.in) == 1)
            System.exit(1);
    }
}


