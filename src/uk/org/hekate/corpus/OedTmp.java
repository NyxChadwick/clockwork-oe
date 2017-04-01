package uk.org.hekate.corpus;

import org.jetbrains.annotations.NotNull;
import uk.org.hekate.utility.Console;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class OedTmp {
    public static void OedRead() {
        Console.writeLine("Reading source file.");

        String[] words = ReadDistinctWordSet();

        Arrays.sort(words);

        for (String word: words)
        {
            if (word.contains("'"))
            {
                Console.writeLine(">> " + word + " is a short-form and cannot be looked up.");
            }
            else if (word.length() > 3 && word.endsWith("ed"))
            {
                Console.writeLine(">> " + word + " is possibly a past tense.");
            }
            else if (word.length() > 4 &&
                    (word.endsWith("ches") || word.endsWith("shes") || word.endsWith("sses") || word.endsWith("thes")))
            {
                Console.writeLine(">> " + word + " is possibly an 'es' plural.");
            }
            else if (word.length() > 3 && word.endsWith("xes"))
            {
                Console.writeLine(">> " + word + " is possibly an 'xes' plural.");
            }
            else if (word.length() > 4 && word.endsWith("ing"))
            {
                Console.writeLine(">> " + word + " is possibly an 'xes' plural.");
            }
            else
            {
                Console.writeLine(">> " + word + " is a standard lookup.");
            }
        }
    }


    @NotNull
    private static String[] ReadDistinctWordSet() {
        Set<String> set = new HashSet<>();

        try {

            try (Reader reader = new InputStreamReader(new FileInputStream("C:\\Temp\\words.txt"), "UTF-8")) {
                StringBuilder word = new StringBuilder();
                int input;

                while ((input = reader.read()) != -1) {
                    char character = (char) input;

                    if (character == '’') {
                        character = '\'';
                    }

                    if (Character.isLetter(character) ||
                            (word.length() > 0 && Character.isLetter(word.charAt(word.length() - 1)) &&
                                    (character == '-' || character == '\''))) {
                        word.append(Character.toLowerCase(character));
                    } else if (word.length() > 0) {
                        int length = word.length();

                        if (!Character.isLetter(word.charAt(length - 1))) {
                            word.delete(length - 1, length);
                            --length;
                        }
                        set.add(word.toString());
                        word.delete(0, length);
                    }
                }

                if (word.length() > 0) {
                    set.add(word.toString());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return set.toArray(new String[set.size()]);
    }
}
