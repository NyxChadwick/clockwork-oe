package uk.org.hekate.corpus;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import uk.org.hekate.utility.Console;
import uk.org.hekate.utility.Xml;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

import static uk.org.hekate.utility.Console.Colour.*;


public class BncLoader {
    @NotNull private final Map<String, List<Word>> _map;
    @NotNull private final Xml.Query _query;


    public BncLoader() throws XPathExpressionException, ParserConfigurationException {
        _map = new HashMap<>();
        _query = new Xml.Query("/bncDoc/wtext//w");
    }


    public void appendXmlFile(@NotNull String filename) throws XPathExpressionException, SAXException, IOException, ParserConfigurationException {
        appendXmlFile(filename, null);
    }


    public void appendXmlFile(@NotNull String filename, Console.ConsoleState console) throws XPathExpressionException, SAXException, IOException, ParserConfigurationException {
        List<Node> nodes = _query.execute(new File(filename));

        int newDefinitions = 0;

        for (Node node: nodes) {
            NamedNodeMap attributes = node.getAttributes();

            String type = attributes.getNamedItem("pos").getTextContent();
            String head = attributes.getNamedItem("hw").getTextContent();
            String category = attributes.getNamedItem("c5").getTextContent();
            String text = node.getTextContent().trim();
            String word = text.toLowerCase();

            List<Word> definitions = _map.getOrDefault(head, null);

            if (definitions == null) {
                definitions = new ArrayList<>();
                _map.put(head, definitions);
            }

            Word definition = null;

            for (Word candidate : definitions)
            {
                if (candidate.word.equals(word) && candidate.type.equals(type) && candidate.category.equals(category))
                {
                    definition = candidate;
                    break;
                }
            }

            if (definition == null)
            {
                definition = new Word(word, type, category);
                definitions.add(definition);
                ++newDefinitions;

                if (console != null)
                {
                    console.setForeground(Green);
                    console.writeLine("Word: " + type + " (" + category + ") " + head + " (" + text + ")");
                }
            }
            else
            {
                ++definition.count;
            }
        }

        if (console != null) {
            console.writeLine();
            console.setForeground(Magenta);
            console.writeLine(Integer.toString(nodes.size()) + " nodes");
            console.writeLine(Integer.toString(newDefinitions) + " new words");
            console.writeLine(Integer.toString(_map.size()) + " head-words");
            console.writeLine();
            console.setForeground(null);
        }
    }


    public void appendXmlFolder(@NotNull String filename) throws XPathExpressionException, IOException, SAXException, ParserConfigurationException {
        appendXmlFolder(filename, null);
    }


    public void appendXmlFolder(@NotNull String filename, Console.ConsoleState console) throws IOException, XPathExpressionException, SAXException, ParserConfigurationException {
        Queue<File> folders = new LinkedList<>();

        folders.add(new File(filename));

        while (!folders.isEmpty()) {
            for (File entry : folders.remove().listFiles()) {
                if (entry.isDirectory()) {
                    folders.add(entry);
                } else if (entry.isFile() && entry.getName().endsWith(".xml")) {
                    if (console != null) {
                        console.setForeground(Blue);
                        console.writeLine(">> Found file: " + entry.getName());
                        console.writeLine();
                    }
                    appendXmlFile(entry.getAbsolutePath(), console);
                }
            }
        }
    }


    public Map<String, List<Word>> getMap() { return Collections.unmodifiableMap(_map); }


    public void load(@NotNull String filename) throws IOException {
        try (FileReader reader = new FileReader(filename)) {
            Type type = new TypeToken<HashMap<String, ArrayList<Word>>>(){}.getType();
            HashMap<String, ArrayList<Word>> data = new Gson().fromJson(reader, type);

            _map.clear();
            _map.putAll(data);
        }
    }


    public void save(@NotNull String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(new Gson().toJson(_map));
        }
    }


    public static class Word {
        public int count = 1;
        @NotNull public final String category;
        @NotNull public final String type;
        @NotNull public final String word;

        Word(@NotNull String word, @NotNull String type, @NotNull String category) {
            this.category = category;
            this.type = type;
            this.word = word;
        }
    }
}
