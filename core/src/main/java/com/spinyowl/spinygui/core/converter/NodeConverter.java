package com.spinyowl.spinygui.core.converter;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.EmptyElement;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.Text;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Node marshaller. Used to convert node to xml and vise versa.
 */
@Slf4j
public final class NodeConverter {

  private static final List<String> EMPTY_ELEMENTS = List.of(
      "area", "base", "br", "col", "embed", "hr", "img", "input",
      "keygen", "link", "meta", "param", "source", "track", "wbr"
  );

  private NodeConverter() {
  }

  /**
   * Used to convert node tree to xml.
   *
   * @param node node to convert.
   * @return string with xml representation of provided node.
   */
  public static String toXml(Node node) {
    return toXml(node, true);
  }

  /**
   * Used to convert node with all child elements to an xml string.
   *
   * @param node   node to convert.
   * @param pretty defines if should be pretty printed.
   * @return String with xml representation of node.
   */
  public static String toXml(Node node, boolean pretty) {
    if (node == null) {
      return null;
    }
    StringWriter stringWriter = new StringWriter();

    try {
      DocumentBuilder builder = DocumentBuilderFactory.newDefaultInstance().newDocumentBuilder();
      Document document = builder.newDocument();

      org.w3c.dom.Node content = createContent(document, node);
      document.appendChild(content);

      StreamResult result = new StreamResult(stringWriter);
      TransformerFactory tf = TransformerFactory.newDefaultInstance();
      Transformer transformer = tf.newTransformer();
      if (pretty) {
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
      }
      transformer.transform(new DOMSource(document), result);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return stringWriter.toString();
  }

  private static org.w3c.dom.Node createContent(Document document, Node node) {
    if (node instanceof Text) {
      return document.createTextNode(((Text) node).content());
    } else if (node instanceof Element) {
      return createElement(document, (Element) node);
    } else {
      log.warn("Attempt to marshal {} class which is not Text or Element child -> skipping.",
          node.getClass().getName());
      return null;
    }
  }

  private static org.w3c.dom.Element createElement(Document document, Element node) {
    String name = node.nodeName().toLowerCase();
    org.w3c.dom.Element element = document.createElement(name);

    for (var entry : node.attributes().entrySet()) {
      element.setAttribute(entry.getKey(), entry.getValue());
    }

    if (!EMPTY_ELEMENTS.contains(name)) {
      for (Node childNode : node.childNodes()) {
        org.w3c.dom.Node content = createContent(document, childNode);
        if (content != null) {
          element.appendChild(content);
        }
      }
    }

    return element;
  }

  // unmarshaller section

  @SneakyThrows
  public static Node fromXml(String xml) {
    if (xml == null || xml.isEmpty()) {
      return null;
    }

    DocumentBuilderFactory f = DocumentBuilderFactory.newDefaultInstance();
    f.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
    f.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
    DocumentBuilder documentBuilder = f.newDocumentBuilder();

    InputSource inputSource = new InputSource();
    inputSource.setCharacterStream(new StringReader((xml)));
    Document document = documentBuilder.parse(inputSource);

    return createNodeFromContent(document.getDocumentElement());
  }

  private static Node createNodeFromContent(org.w3c.dom.Node node) {
    if (node instanceof org.w3c.dom.Text) {
      org.w3c.dom.Text text = (org.w3c.dom.Text) node;
      String wholeText = text.getWholeText();
      if (wholeText.isEmpty()) {
        return null;
      }
      return new Text(wholeText);
    } else if (node instanceof org.w3c.dom.Element) {
      return createNodeFromElement((org.w3c.dom.Element) node);
    } else {
      log.warn("Content type '{}' is not supported. Content value '{}'.", node.getNodeType(),
          node.getNodeValue());
      return null;
    }
  }

  private static Node createNodeFromElement(org.w3c.dom.Element element) {
    String tagName = element.getTagName().toLowerCase();
    Node instance;
    if (EMPTY_ELEMENTS.contains(tagName)) {
      instance = new EmptyElement(tagName);
    } else {
      instance = new Element(tagName);
      NodeList childNodes = element.getChildNodes();
      for (int i = 0; i < childNodes.getLength(); i++) {
        org.w3c.dom.Node c = childNodes.item(i);
        try {
          Node componentFromContent = createNodeFromContent(c);
          if (componentFromContent != null) {
            instance.addChild(componentFromContent);
          }
        } catch (Exception e) {
          log.error(e.getMessage(), e);
        }
      }
    }
    NamedNodeMap attributes = element.getAttributes();
    for (int i = 0; i < attributes.getLength(); i++) {
      org.w3c.dom.Attr attr = (Attr) attributes.item(i);
      instance.attributes().put(attr.getName(), attr.getValue());
    }

    return instance;
  }
}
