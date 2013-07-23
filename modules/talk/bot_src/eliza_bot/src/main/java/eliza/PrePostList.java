package eliza;

import java.util.ArrayList;

public class PrePostList extends ArrayList<PrePost>
{

    /**
     * Add another entry to the list.
     */
    public void add(String pre, String post)
    {
        add(new PrePost(pre, post));
    }

    public String translate(String str)
    {
        String lines[] = new String[2];
        String ret = Utils.trimLeading(str);
        str = "";
        while (Utils.match(ret, "* *", lines))
        {
            str += transform(lines[0]) + " ";
            ret = Utils.trimLeading(lines[1]);
        }
        str += transform(ret);
        return str;
    }

    public String transform(String str)
    {
        for (PrePost p : this)
        {
            if (str.equals(p.getPre()))
                return p.getPost();
        }
        return str;
    }
}
