package eliza;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.joda.time.DateTime;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Utils
{
    private static final String digits = "0123456789";
    
    public static int count(String str, char c)
    {
        int count = 0;
        for (int i = 0; i < str.length(); i++)
            if (str.charAt(i) == c)
                count++;
        return count;
    }
    
    public static int countFirstDigits(String str)
    {
        int count = 0;
        for (int i = 0; i < str.length(); i++)
        {
            if (digits.indexOf(str.charAt(i)) == -1)
                return count;
            count++;
        }
        return count;
    }
    
    /**
     * Find the number of characters that are equal to * or #
     * or that literally match the pattern
     */
    public static int countMatch(String str, String pat)
    {
        int strIndex = 0; 
        int patIndex = 0;
        int count = 0;
        while (strIndex < str.length() && patIndex < pat.length())
        {
            char p = pat.charAt(patIndex);
            if (p == '*' || p == '#')
                return count;
            if (str.charAt(strIndex) != p)
                return -1;
            strIndex++;
            patIndex++;
            count++;
        }
        return count;
    }

    public static int countMatchInPattern(String str, String pat)
    {
        int count = 0;
        for (int i = 0; i < str.length(); i++)
        {
            if (countMatch(str.substring(i), pat) >= 0)
                return count;
            count++;
        }
        return -1;
    }

    /**
     * - Match the string against the pattern
     * - put into `matches` array all substrings that match * and/or #
     */
    private static boolean doMatch(String str, String pat, String matches[])
    {
        int strIndex = 0;
        int iMatch = 0;
        int patIndex = 0;
        while (patIndex < pat.length() && iMatch < matches.length)
        {
            char p = pat.charAt(patIndex);
            if (p == '*')
            {
                int n;
                if (patIndex + 1 == pat.length())
                {
                    //  * is the last thing in pat
                    //  n is remaining string length
                    n = str.length() - strIndex;
                }
                else
                {
                    //  * is not last in pat
                    //  find using remaining pat
                    n = countMatchInPattern(str.substring(strIndex), pat.substring(patIndex + 1));
                }
                if (n < 0)
                    return false;
                matches[iMatch++] = str.substring(strIndex, strIndex + n);
                strIndex += n;
                patIndex++;
            }
            else if (p == '#')
            {
                int n = countFirstDigits(str.substring(strIndex));
                matches[iMatch++] = str.substring(strIndex, strIndex + n);
                strIndex += n;
                patIndex++;
            }
            else
            {
                int n = countMatch(str.substring(strIndex), pat.substring(patIndex));
                if (n <= 0)
                    return false;
                strIndex += n;
                patIndex += n;
            }
        }
        if (strIndex >= str.length() && patIndex >= pat.length())
            return true;
        return false;
    }

    public static boolean match(String str, String pat, String matches[])
    {
        return doMatch(str, pat, matches);
    }

    public static String translate(String str, String src, String target)
    {
        if (src.length() != target.length())
        {
            throw new IllegalArgumentException("SRC and TARGET have to have the same length");
        }

        for (int i = 0; i < src.length(); i++)
        {
            str = str.replace(src.charAt(i), target.charAt(i));
        }
        return str;
    }

    public static String compress(String s)
    {
        String retVal = "";
        if (s.length() == 0)
            return s;
        char c = s.charAt(0);
        for (int i = 1; i < s.length(); i++)
        {
            if (c == ' '
                && ((s.charAt(i) == ' ')
                    || (s.charAt(i) == ',')
                    || (s.charAt(i) == '.')))
            {
                // no -op
            }
            else if (c != ' ' && s.charAt(i) == '?')
            {
                retVal += c + " ";
            }
            else
            {
                retVal += c;
            }
            c = s.charAt(i);
        }
        retVal += c;
        return retVal;
    }

    public static String trimLeading(String str)
    {
        for (int i = 0; i < str.length(); i++)
        {
            if (!Character.isWhitespace(str.charAt(i)))
                return str.substring(i);
        }
        return "";
    }

    public static String pad(String s)
    {
        if (s.length() == 0)
            return " ";
        char first = s.charAt(0);
        char last = s.charAt(s.length() - 1);
        if (first == ' ' && last == ' ')
            return s;
        if (first == ' ' && last != ' ')
            return s + " ";
        if (first != ' ' && last == ' ')
            return " " + s;
        if (first != ' ' && last != ' ')
            return " " + s + " ";

        // TODO: unreachable
        return s;
    }
    
    
    public static String getCurrentWeather()
    {
        try
        {
            URL json = new URL(weather);
          
          JSONObject jsonObj = (JSONObject) JSONValue.parse(new InputStreamReader(json.openStream()));
          
          String temp = ((JSONObject)jsonObj.get("current_observation")).get("temperature_string").toString();
          String w = ((JSONObject)jsonObj.get("current_observation")).get("weather").toString();
          String loc = ((JSONObject)((JSONObject)jsonObj.get("current_observation")).get("display_location")).get("full").toString();
          
          return temp + ", " + w + " in " + loc;
        }
        catch (MalformedURLException ex)
        {
            //
            return "<UNKNOWN>";
        }
        catch (IOException e)
        {
            return "<UNKNOWN>";
        }
    }
    
    public static void main(String[] args) throws MalformedURLException, ParserConfigurationException, IOException
    {
        
        
        System.out.println(getCurrentWeather());
        

    }
    
    private static final String weather = "http://api.wunderground.com/api/de13f2e6a2c425f6/conditions/q/MA/Newton.json";
}