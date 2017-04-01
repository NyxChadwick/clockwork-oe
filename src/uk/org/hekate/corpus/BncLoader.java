package uk.org.hekate.corpus;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import uk.org.hekate.utility.Console;
import uk.org.hekate.xml.XmlParser;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

import static uk.org.hekate.utility.Console.Colour.*;


public class BncLoader {
    @NotNull private final HashMap<String, List<Definition>> _map;
    @NotNull private final XmlParser _xmlParser;


    public BncLoader() throws XPathExpressionException, ParserConfigurationException {
        _map = new HashMap<>();
        _xmlParser = new XmlParser("/bncDoc/wtext//w");
    }


    public void loadJson(@NotNull String filename) throws IOException {
        try (FileReader reader = new FileReader(filename)) {
            Type type = new TypeToken<Map<String, List<Definition>>>(){}.getType();
            Map<String, List<Definition>> map = new Gson().fromJson(reader, type);

            _map.clear();
            _map.putAll(map);
        }
    }


    public void loadXml(@NotNull String filename) throws XPathExpressionException, SAXException, IOException {
        loadXml(filename, null);
    }


    public void loadXml(@NotNull String filename, Console.ConsoleState console) throws XPathExpressionException, SAXException, IOException {
        List<Node> nodes = _xmlParser.parse(new File(filename));

        int newDefinitions = 0;

        for (Node node: nodes) {
            NamedNodeMap attributes = node.getAttributes();

            String type = attributes.getNamedItem("pos").getTextContent();
            String head = attributes.getNamedItem("hw").getTextContent();
            String category = attributes.getNamedItem("c5").getTextContent();
            String text = node.getTextContent().trim();
            String word = text.toLowerCase();

            List<Definition> definitions = _map.getOrDefault(head, null);

            if (definitions == null) {
                definitions = new ArrayList<>();
                _map.put(head, definitions);
            }

            Definition definition = null;

            for (Definition candidate : definitions)
            {
                if (candidate.word.equals(word) && candidate.type.equals(type) && candidate.category.equals(category))
                {
                    definition = candidate;
                    break;
                }
            }

            if (definition == null)
            {
                definition = new Definition(word, type, category);
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


    public void loadXmlTree(@NotNull String filename) throws XPathExpressionException, IOException, SAXException {
        loadXmlTree(filename, null);
    }


    public void loadXmlTree(@NotNull String filename, Console.ConsoleState console) throws IOException, XPathExpressionException, SAXException {
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
                    loadXml(entry.getAbsolutePath(), console);
                }
            }
        }
    }


    public void saveJson(@NotNull String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            new Gson().toJson(_map, writer);
        }
    }


    private static class Definition {
        int count = 1;
        @NotNull private final String category;
        @NotNull private final String type;
        @NotNull private final String word;

        Definition(@NotNull String word, @NotNull String type, @NotNull String category) {
            this.category = category;
            this.type = type;
            this.word = word;
        }
    }
}
