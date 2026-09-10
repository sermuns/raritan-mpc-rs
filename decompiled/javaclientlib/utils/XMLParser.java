/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.utils;

import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLParser {
    public Document getDocument(String string) throws ParserConfigurationException, SAXException, IOException {
        Document document = null;
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource inputSource = new InputSource(new StringReader(string));
        document = documentBuilder.parse(inputSource);
        return document;
    }

    public static Document getXMLDocument(String string) throws SAXException {
        assert (string != null);
        Document document = null;
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        }
        catch (ParserConfigurationException parserConfigurationException) {
            parserConfigurationException.printStackTrace();
        }
        if (documentBuilder != null) {
            InputSource inputSource = new InputSource(new StringReader(string));
            try {
                document = documentBuilder.parse(inputSource);
            }
            catch (IOException iOException) {
                iOException.printStackTrace();
            }
        }
        return document;
    }

    public static String getTextContent(Node node) {
        Node node2;
        NodeList nodeList = node.getChildNodes();
        if (nodeList.getLength() == 1 && (node2 = nodeList.item(0)).getNodeType() == 3) {
            return node2.getNodeValue();
        }
        return null;
    }
}

