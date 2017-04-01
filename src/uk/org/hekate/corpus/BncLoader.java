package uk.org.hekate.corpus;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import uk.org.hekate.utility.Console;
import uk.org.hekate.xml.XmlParser;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class BncLoader {
    @NotNull private final HashMap<String, List<Definition>> _map;
    @NotNull private final XmlParser _xmlParser;


    public BncLoader() throws XPathExpressionException, ParserConfigurationException {
        _map = new HashMap<>();
        _xmlParser = new XmlParser("/bncDoc/wtext//w");
    }


    public void load(@NotNull String filename) throws XPathExpressionException, SAXException, IOException {
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

                Console.writeLine("Word: " + type + " (" + category + ") " + head + " (" + text + ")");
            }
            else
            {
                ++definition.count;
            }
        }

        Console.writeLine();
        Console.writeLine(Integer.toString(nodes.size()) + " nodes");
        Console.writeLine(Integer.toString(newDefinitions) + " words");
        Console.writeLine(Integer.toString(_map.size()) + " head-words");
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
