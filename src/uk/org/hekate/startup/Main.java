package uk.org.hekate.startup;

import org.xml.sax.SAXException;
import uk.org.hekate.corpus.BncLoader;
import uk.org.hekate.utility.Console;

import javax.xml.parsers.*;
import javax.xml.xpath.*;
import java.io.*;
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
            BncLoader parser = new BncLoader();
            Queue<File> folders = new LinkedList<>();

            folders.add(new File("C:\\Users\\Prooskalia\\Desktop\\2554\\2554\\download\\Texts"));

            while (!folders.isEmpty())
            {
                for (File entry: folders.remove().listFiles())
                {
                    if (entry.isDirectory())
                    {
                        folders.add(entry);
                    }
                    else
                    {
                        if (entry.isFile() && entry.getName().endsWith(".xml"))
                        {
                            console.writeLine(">> Found file: " + entry.getName());
                            console.writeLine();
                            parser.load(entry.getAbsolutePath());
                            console.writeLine();
                        }
                    }
                }
            }
        } catch (XPathExpressionException | ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        console.writeLine();
        console.setForeground(Red);
        console.writeLine("<< FINISHED");
    }
}
