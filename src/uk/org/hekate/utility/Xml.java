package uk.org.hekate.utility;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.AbstractList;
import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;


public final class Xml {
    private Xml() { }


    @NotNull public static List<Node> asList(NodeList n) {
        return (n == null || n.getLength()==0)? Collections.emptyList(): new Xml.NodeListWrapper(n);
    }


    @NotNull public static Query generateQuery(@NotNull String queryString) throws XPathExpressionException {
        return new Query(queryString);
    }


    @NotNull public static Document parse(@NotNull File file) throws IOException, SAXException, ParserConfigurationException {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
    }


    @NotNull public static Document parse(@NotNull String text) throws IOException, SAXException, ParserConfigurationException {
        try (InputStream stream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8))) {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stream);
        }
    }


    private static final class NodeListWrapper extends AbstractList<Node> implements RandomAccess {
        private final int _length;
        @NotNull private final NodeList _list;

        NodeListWrapper(@NotNull NodeList list) {
            _list = list;
            _length = list.getLength();
        }

        @NotNull public Node get(int index) { return _list.item(index); }

        public int size() { return _length; }
    }


    public static final class Query {
        @NotNull private final XPathExpression _query;

        public Query(@NotNull String queryString) throws XPathExpressionException {
            _query = XPathFactory.newInstance().newXPath().compile(queryString);
        }


        @NotNull public List<Node> execute(@NotNull File file) throws IOException, SAXException, XPathExpressionException, ParserConfigurationException {
            return Xml.asList((NodeList) _query.evaluate(parse(file), XPathConstants.NODESET));
        }


        @NotNull public List<Node> execute(@NotNull String text) throws IOException, SAXException, XPathExpressionException, ParserConfigurationException {
            return Xml.asList((NodeList) _query.evaluate(parse(text), XPathConstants.NODESET));
        }
    }
}
