package eliza;

public class PrePost
{
    private final String pre;
    private final String post;

    PrePost(String pre, String post)
    {
        this.pre = pre;
        this.post = post;
    }

    public String getPre()
    {
        return pre;
    }

    public String getPost()
    {
        return post;
    }
}
