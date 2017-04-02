package uk.org.hekate.startup;

import uk.org.hekate.corpus.*;
import uk.org.hekate.utility.Console;

import java.util.*;

import static uk.org.hekate.utility.Console.Colour.*;


public class Main {
    public static void main(String[] args) {
        Console.ConsoleState console = new Console.ConsoleState();

        console.setAnsiColourAutomatic();
        console.setForeground(Red);
        console.writeLine(">> STARTING");
        console.setForeground(Blue);
        console.writeLine();

        try {
            BncLoader loader = new BncLoader();

            loader.load("C:\\Temp\\SavedBNC.json");
            Map<Integer, Integer> definitions = new HashMap();
            Map<Integer, Integer> occurrences = new HashMap();
            List<String> highFrequency = new ArrayList<String>();
            int largestSize = 0;
            String largestWord = null;

            for (Map.Entry<String, List<BncLoader.Word>> entry: loader.getMap().entrySet())
            {
                int size = entry.getValue().size();
                Integer value = definitions.getOrDefault(size, null);

                if (value == null)
                {
                    definitions.put(size, 1);
                }
                else
                {
                    if (size > largestSize)
                    {
                        largestSize = size;
                        largestWord = entry.getKey();
                    }
                    definitions.put(size, value + 1);
                }

                size = (int)Math.ceil(Math.log10(entry.getValue().stream().mapToInt(w -> w.count).sum()));
                value = occurrences.getOrDefault(size, null);

                if (value == null)
                {
                    occurrences.put(size, 1);
                }
                else
                {
                    occurrences.put(size, value + 1);

                    if (size > 5)
                    {
                        highFrequency.add(entry.getKey());
                    }
                }
            }

            console.setForeground(Cyan);

            for (Map.Entry<Integer, Integer> entry: occurrences.entrySet())
            {
                console.writeLine("{0} => {1}", entry.getKey(), entry.getValue());
            }

            console.writeLine();

            for (String key: highFrequency)
            {
                console.writeLine(key);
            }

            console.writeLine();

            for (Map.Entry<Integer, Integer> entry: definitions.entrySet())
            {
                 console.writeLine("{0} => {1}", entry.getKey(), entry.getValue());
            }

            console.writeLine();
            console.setForeground(Yellow);
            console.writeLine(largestWord);
            console.writeLine();

            ArrayList<BncLoader.Word> words = (ArrayList<BncLoader.Word>)loader.getMap().get(largestWord);

            Collections.sort(words, (first, second) ->
                    first.type.compareTo(second.type) != 0? first.type.compareTo(second.type):
                    first.word.equals(second.word)? ((second.count > first.count)? 1: -1):
                    first.word.compareTo(second.word));

            int noun = 0;
            int nounMaybe = 0;
            int verb = 0;
            int verbMaybe = 0;
            int pronoun = 0;
            int pronounMaybe = 0;
            int adjective = 0;
            int adjectiveMaybe = 0;
            int adverb = 0;
            int adverbMaybe = 0;
            int uncategorised = 0;

            for (BncLoader.Word word: words)
            {
                switch (word.type)
                {
                    case "ADV": adverb += word.count; break;
                    case "ADJ": adjective += word.count; break;
                    case "PRON": noun += word.count; break;
                    case "SUBST": noun += word.count; break;
                    case "VERB": verb += word.count; break;
                    default: uncategorised += word.count; break;
                }
                int dash = word.category.indexOf('-');

                if (dash >= 0)
                {
                    switch (word.category.substring(dash + 1, dash + 3))
                    {
                        case "AJ": adjectiveMaybe += word.count; break;
                        case "AV": adverbMaybe += word.count; break;
                        case "NN": nounMaybe += word.count; break;
                        case "NP": nounMaybe += word.count; break;
                        case "VV": verbMaybe += word.count; break;
                    }
                }
                console.writeLine("{0}: {1} {2} {3}", word.word, word.type, word.category, word.count);
            }
            console.writeLine();
            console.writeLine("Nouns: {0}  Maybe {1}", noun, nounMaybe);
            console.writeLine("Verbs: {0}  Maybe {1}", verb, verbMaybe);
            console.writeLine("Adjectives: {0}  Maybe {1}", adjective, adjectiveMaybe);
            console.writeLine("Adverbs: {0}  Maybe {1}", adverb, adverbMaybe);
            console.writeLine("Pronouns: {0}  Maybe {1}", pronoun, pronounMaybe);
            console.writeLine("Unknown: {0}", uncategorised);
            console.writeLine();

        } catch (Exception e) {
            e.printStackTrace();
        }

//        try {
//            BncLoader parser = new BncLoader();
//
//            parser.appendXmlFolder("C:\\Users\\Prooskalia\\Desktop\\2554\\2554\\download\\Texts", console);
//            parser.save("C:\\Temp\\SavedBNC.json");
//
//        } catch (XPathExpressionException | ParserConfigurationException | SAXException | IOException e) {
//            e.printStackTrace();
//        }

        console.writeLine();
        console.setForeground(Red);
        console.writeLine("<< FINISHED");
    }
}
