package eliza;

import java.util.ArrayList;
import java.util.List;

/**
 * Convenient class representing a list of decompositions of each key.
 */
public class DecompositionList extends ArrayList<Decomposition>
{
    public void add(String word, boolean isMemory, List<String> responses)
    {
        add(new Decomposition(word, isMemory, responses));
    }
}
