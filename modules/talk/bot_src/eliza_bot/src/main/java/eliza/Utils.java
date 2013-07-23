package eliza;

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
}