package eliza;

import java.util.List;
import java.util.Random;

public class Decomposition
{
    private final String decompositionPattern;
    private final boolean isMemory;
    private final List<String> responseList;
    private int curResponseIndex;
    private final Random rand = new Random();

    public Decomposition(String pattern, boolean mem, List<String> responseList)
    {
        this.decompositionPattern = pattern;
        this.isMemory = mem;
        this.responseList = responseList;
        this.curResponseIndex = 100;
    }
    public String getPattern()
    {
        return decompositionPattern;
    }

    public boolean isMemory()
    {
        return isMemory;
    }

    public String getNextRule()
    {
        if (responseList.isEmpty())
        {
            System.err.println("No rule available!");
            return null;
        }
        return responseList.get(curResponseIndex);
    }

    /**
     * advance to the next rule (either randomly, if in memory or the next one int he list)
     */
    public void advance()
    {
        int size = responseList.size();
        if (isMemory)
        {
            curResponseIndex = rand.nextInt(size);
        }
        curResponseIndex++;
        if (curResponseIndex >= size)
        {
            curResponseIndex = 0;
        }
    }
}
