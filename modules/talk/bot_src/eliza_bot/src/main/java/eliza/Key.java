package eliza;

public class Key
{
    private String key;
    private int rank;
    private DecompositionList decompositions;

    public Key(String key, int rank, DecompositionList decomp)
    {
        this.key = key;
        this.rank = rank;
        this.decompositions = decomp;
    }

    public Key()
    {
        key = null;
        rank = 0;
        decompositions = null;
    }

    public void copyFrom(Key k)
    {
        key = k.keyName();
        rank = k.keyRank();
        decompositions = k.decompositions();
    }

    public String keyName()
    {
        return key;
    }

    public int keyRank()
    {
        return rank;
    }

    public DecompositionList decompositions()
    {
        return decompositions;
    }
}
