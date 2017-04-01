package uk.org.hekate.xml;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.util.List;


public class XmlParser {
    @NotNull private final DocumentBuilder _documentBuilder;
    @NotNull private final XPathExpression _query;


    public XmlParser(@NotNull String queryString) throws XPathExpressionException, ParserConfigurationException {

        _query = XPathFactory
                .newInstance()
                .newXPath()
                .compile(queryString);

        _documentBuilder = DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder();
    }


    @NotNull public List<Node> parse(@NotNull File file) throws IOException, SAXException, XPathExpressionException {
        return XmlNodes.asList((NodeList) _query.evaluate(_documentBuilder.parse(file), XPathConstants.NODESET));
    }
}
