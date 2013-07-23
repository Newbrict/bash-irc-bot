package eliza;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Synonyms
{
    // map a word to a set of synonyms 
    // TODO: This increases performance, but needs a LOT of mem!
    private final Map<String, Set<String>> synonymsMap;
    
    public Synonyms()
    {
        synonymsMap = new HashMap<>();
    }

    /**
     * Add another word list the the synonym list.
     * Each word in the list will be a key
     */
    public void add(Collection<String> words)
    {
        final Set<String> synWords = new HashSet<>(words);
        for (String word : synWords)
            synonymsMap.put(word, synWords);
    }

    public boolean matchDecomposition(String str, String pat, String lines[])
    {
        if (!Utils.match(pat, "*@* *", lines))
        {
            //  no synonyms in decomp pattern
            return Utils.match(str, pat, lines);
        }
         
        String first = lines[0];
        String synWord = lines[1];
        String theRest = " " + lines[2];
        //  Look up the synonyms map
        Set<String> syns = synonymsMap.get(synWord);
        if (syns == null)
        {
            System.err.println("No synonyms available for " + synWord);
            return false;
        }
        //  Try each synonym
        for (String syn : syns)
        {
            pat = first + syn + theRest;
            if (Utils.match(str, pat, lines))
            {
                int n = Utils.count(first, '*');
                //  Make room for the synonym in the match list.
                for (int j = lines.length - 2; j >= n; j--)
                    lines[j + 1] = lines[j];
                //  The synonym goes in the match list.
                lines[n] = syn;
                return true;
            }
        }
        return false;
    }
}
