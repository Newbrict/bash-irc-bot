package eliza;

import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * 
 * Convenient class representing a list of keys
 */
public class KeysList extends ArrayList<Key>
{
    public void computeKeysQueue(PriorityQueue<Key> keys, String s)
    {
        keys.clear();
        s = Utils.trimLeading(s);
        String lines[] = new String[2];
        Key k;
        while (Utils.match(s, "* *", lines))
        {
            k = getKey(lines[0]);
            if (k != null)
                keys.offer(k);
            s = lines[1];
        }
        k = getKey(s);
        if (k != null)
            keys.offer(k);
    }
    
    public void add(String key, int rank, DecompositionList decomp)
    {
        add(new Key(key, rank, decomp));
    }
    
    public Key getKey(String s)
    {
        for (int i = 0; i < size(); i++)
        {
            Key key = get(i);
            if (s.equals(key.keyName()))
                return key;
        }
        return null;
    }
}
