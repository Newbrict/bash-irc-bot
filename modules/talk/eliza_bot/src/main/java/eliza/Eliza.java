package eliza;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Stack;

public class Eliza
{
    private static final int KEYS_COUNT = 30;
    private final KeysList keysList = new KeysList();
    private final Synonyms syns = new Synonyms();
    private final PrePostList pre = new PrePostList();
    private final PrePostList post = new PrePostList();
    private String initialMsg = "Hello.";
    private String finalMsg = "Goodbye.";
    private List<String> quitMsges = new ArrayList<>();
    private PriorityQueue<Key> keysQueue
            = new PriorityQueue<>(KEYS_COUNT,
                                  new Comparator<Key>()
                                  {
                                      // reverse order (ie., largest first)
                                      @Override
                                      public int compare(Key o1, Key o2)
                                      {
                                          return o2.keyRank() - o1.keyRank();
                                      }
                                  });
    
    private Stack<String> mem = new Stack<>();
    private DecompositionList lastDecomp;
    private List<String> lastResponses;
    boolean finished = false;

    public Eliza(InputStream scriptStream)
    {
        readScript(scriptStream);
    }
    
    public boolean finished()
    {
        return finished;
    }

    final private void readScript(InputStream inputStream)
    {
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null)
            {
                processScriptLine(line);
            }
        }
        catch (IOException e)
        {
            System.err.println("Error reading script file: " + e);
        }
    }
        
    /**
     * process a line of script input.
     */
    private final void processScriptLine(String s)
    {
        String lines[] = new String[4];

        if (Utils.match(s, "*response: *", lines))
        {
            if (lastResponses == null)
            {
                System.err.println("Error: no last responses available");
                return;
            }
            lastResponses.add(lines[1]);
        }
        else if (Utils.match(s, "*decomposition: *", lines))
        {
            if (lastDecomp == null)
            {
                System.err.println("Error: no last decomposition available");
                return;
            }
            lastResponses = new ArrayList<>();
            String temp = lines[1];
            if (Utils.match(temp, "$ *", lines))
            {
                lastDecomp.add(lines[0], true, lastResponses);
            }
            else
            {
                lastDecomp.add(temp, false, lastResponses);
            }
        }
        else if (Utils.match(s, "*key: * #*", lines))
        {
            lastDecomp = new DecompositionList();
            lastResponses = null;
            int n = 0;
            if (lines[2].length() != 0)
            {
                try
                {
                    n = Integer.parseInt(lines[2]);
                }
                catch (NumberFormatException e)
                {
                    System.err.println(e.getMessage());
                }
            }
            keysList.add(lines[1], n, lastDecomp);
        }
        else if (Utils.match(s, "*key: *", lines))
        {
            lastDecomp = new DecompositionList();
            lastResponses = null;
            keysList.add(lines[1], 0, lastDecomp);
        }
        else if (Utils.match(s, "*synon: * *", lines))
        {
            List<String> words = new ArrayList<>();
            words.add(lines[1]);
            s = lines[2];
            while (Utils.match(s, "* *", lines))
            {
                words.add(lines[0]);
                s = lines[1];
            }
            words.add(s);
            syns.add(words);
        }
        else if (Utils.match(s, "*pre: * *", lines))
        {
            pre.add(lines[1], lines[2]);
        }
        else if (Utils.match(s, "*post: * *", lines))
        {
            post.add(lines[1], lines[2]);
        }
        else if (Utils.match(s, "*initial: *", lines))
        {
            initialMsg = lines[1];
        }
        else if (Utils.match(s, "*final: *", lines))
        {
            finalMsg = lines[1];
        }
        else if (Utils.match(s, "*quit: *", lines))
        {
            quitMsges.add(" " + lines[1] + " ");
        }
        else
        {
            System.err.println("Unrecognised input in script file: " + s);
        }
    }

    /**
     * Process a line of input.
     */
    public String processInputLine(String s)
    {
        String reply;
        //  Do some input transformations first.
        s = s.toLowerCase();
        s = Utils.translate(s, "@#$%^&*()_-+=~`{[}]|:;<>\\\"",
                              "                          ");
        s = Utils.translate(s, ",?!", "...");
        //  Compress out multiple speace.
        s = Utils.compress(s);
        String lines[] = new String[2];
        //  Break apart sentences, and do each separately.
        while (Utils.match(s, "*.*", lines))
        {
            reply = sentence(lines[0]);
            if (reply != null)
                return reply;
            s = Utils.trimLeading(lines[1]);
        }
        if (s.length() != 0)
        {
            reply = sentence(s);
            if (reply != null)
                return reply;
        }
        //  Nothing matched, so try memory.
        String m = mem.isEmpty() ? null : mem.pop();
        if (m != null)
            return m;

        //  No memory, reply with xnone.
        Key key = keysList.getKey("xnone");
        if (key != null)
        {
            Key dummy = null;
            reply = decompose(key, s, dummy);
            if (reply != null)
                return reply;
        }
        //  No xnone, just say anything.
        return "My apologies, I am speechless at the moment!";
    }

    /**
     * Process a sentence. 
     * (1) Make pre transformations.
     * (2) Check for quits
     * (3) Scan sentence for keys, build key stack.
     * (4) Try decompositions for each key.
     */
    private String sentence(String s)
    {
        s = pre.translate(s);
        s = Utils.pad(s);
        if (quitMsges.contains(s))
        {
            if (finished)
            {
                // unreachable
                return "We already said goodbye!";
            }
            else
            {
                finished = true;
                return finalMsg; //lastWords;
            }
        }
        keysList.computeKeysQueue(keysQueue, s);
        Iterator<Key> iter = keysQueue.iterator();
        while (iter.hasNext())
        {
            Key gotoKey = new Key();
            String resp = decompose(iter.next(),
                                    s,
                                    gotoKey);
            if (resp != null)
                return resp;
            while (gotoKey.keyName() != null)
            {
                resp = decompose(gotoKey,
                                 s,
                                 gotoKey);
                if (resp != null)
                    return resp;
            }
        }
        return null;
    }


    private String decompose(Key key, String s, Key gotoKey)
    {
        String replies[] = new String[10];
        for (int i = 0; i < key.decompositions().size(); i++)
        {
            Decomposition d = (Decomposition) key.decompositions().get(i);
            String pat = d.getPattern();
            if (syns.matchDecomposition(s, pat, replies))
            {
                String reps = looupResponse(d, replies, gotoKey);
                if (reps != null)
                    return reps;
                if (gotoKey.keyName() != null)
                    return null;
            }
        }
        return null;
    }

    /**
     * Given a decomposition rule and an array of acceptable replies,
     * find the appropriate response:
     *      + if the first response-rule is gotot, return null
     *      + else go look for appropriate response
     */
    private String looupResponse(Decomposition d, String reply[], Key gotoKey)
    {
        String lines[] = new String[3];
        d.advance();
        String rule = d.getNextRule();
        if (Utils.match(rule, "goto *", lines))
        {
            //  goto rule -- set gotoKey and return false.
            gotoKey.copyFrom(keysList.getKey(lines[0]));
            if (gotoKey.keyName() != null)
                return null;
            System.err.println("Goto rule did not match key: " + lines[0]);
            return null;
        }
        String retVal = "";
        while (Utils.match(rule, "* (#)*", lines))
        {
            //  reassembly rule with number substitution
            rule = lines[2];        // there might be more
            int n = 0;
            try
            {
                n = Integer.parseInt(lines[1]) - 1;
            }
            catch (NumberFormatException e)
            {
                System.err.println(e.getMessage());
            }
            if (n < 0 || n >= reply.length)
            {
                System.err.println("Substitution number is bad " + lines[1]);
                return null;
            }
            reply[n] = post.translate(reply[n]);
            retVal += lines[0] + " " + reply[n];
        }
        retVal += rule;
        if (d.isMemory())
        {
            mem.push(retVal);
            return null;
        }
        return retVal;
    }

    //---------------------- public members ------------------------------------
    
    /**
     * same as run(), but do not print prompt ">>>>"
     * @param input
     * @return 
     */
    public int runQuiet(InputStream input)
    {
        Scanner in = new Scanner(input);

        String s;
        System.out.println(initialMsg);
        while (true)
        {
            s = !in.hasNext() ? null : in.nextLine();
            if (s == null)
                break;
            System.out.println(processInputLine(s));
            if (finished)
            {
                return 1;
            }
        }

        return 0;
    }

    public int run(InputStream input)
    {
        Scanner in = new Scanner(input);

        String inputStr;
        inputStr = "Hello.";
        while (true)
        {
            System.out.println(">>>>" + inputStr);
            String reply = processInputLine(inputStr);
            System.out.println(reply);
            if (finished)
                return 1;
            inputStr = in.hasNext() ? null : in.nextLine();
            if (inputStr == null)
                break;
        }

        return 0;
    }

}