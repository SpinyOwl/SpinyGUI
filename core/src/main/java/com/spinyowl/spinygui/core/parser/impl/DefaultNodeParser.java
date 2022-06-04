package com.spinyowl.spinygui.core.parser.impl;

import static com.spinyowl.spinygui.core.node.Frame.FRAME_TAG_NAME;
import static javax.xml.transform.OutputKeys.INDENT;
import static javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.EmptyElement;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.parser.NodeParser;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/** Node marshaller. Used to convert node to xml and vise versa. */
@Slf4j
public final class DefaultNodeParser implements NodeParser {

  private static final List<String> EMPTY_ELEMENTS =
      List.of(
          "area", "base", "br", "col", "embed", "hr", "img", "input", "keygen", "link", "meta",
          "param", "source", "track", "wbr");
  private static final String INDENT_AMOUNT = "{http://xml.apache.org/xslt}indent-amount";

  /**
   * Used to convert node tree to xml.
   *
   * @param node node to convert.
   * @return string with xml representation of provided node.
   */
  public String toXml(@NonNull Node node) {
    return toXml(node, true);
  }

  /**
   * Used to convert node tree to xml.
   *
   * @param node node to convert.
   * @return string with xml representation of provided node.
   */
  public String toXml(@NonNull Node node, boolean pretty) {
    return convertToXml(node, pretty);
  }

  public Node fromXml(@NonNull String xml) {
    try {
      if (xml.isEmpty()) {
        return null;
      }

      var documentBuilderFactory = DocumentBuilderFactory.newDefaultInstance();
      documentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
      documentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
      var documentBuilder = documentBuilderFactory.newDocumentBuilder();

      var inputSource = new InputSource();
      inputSource.setCharacterStream(new StringReader((xml)));
      var document = documentBuilder.parse(inputSource);

      return createNodeFromContent(document.getDocumentElement(), new NodeConverterContext());
    } catch (ParserConfigurationException | SAXException | IOException e) {
      throw new ParseException(e);
    }
  }

  /**
   * Used to convert node with all child elements to a xml string.
   *
   * @param node node to convert.
   * @param pretty defines if node should be pretty printed.
   * @return String with xml representation of node.
   */
  private static String convertToXml(Node node, boolean pretty) {
    if (node == null) {
      return null;
    }
    var stringWriter = new StringWriter();

    try {
      var builder = DocumentBuilderFactory.newDefaultInstance().newDocumentBuilder();
      var document = builder.newDocument();

      org.w3c.dom.Node content = createContent(document, node);
      document.appendChild(content);

      var result = new StreamResult(stringWriter);
      var transformerFactory = TransformerFactory.newDefaultInstance();
      var transformer = transformerFactory.newTransformer();
      if (pretty) {
        transformer.setOutputProperty(INDENT, "yes");
        transformer.setOutputProperty(OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(INDENT_AMOUNT, "2");
      }
      transformer.transform(new DOMSource(document), result);
    } catch (Exception e) {
      if (log.isErrorEnabled()) {
        log.error(e.getMessage(), e);
      }
    }
    return stringWriter.toString();
  }

  private static org.w3c.dom.Node createContent(Document document, Node node) {
    if (node instanceof Text text) {
      return document.createTextNode(text.content());
    } else if (node instanceof Element element) {
      return createElement(document, element);
    } else {
      if (log.isWarnEnabled()) {
        log.warn(
            "Attempt to marshal {} class which is not Text or Element child -> skipping.",
            node.getClass().getName());
      }
      return null;
    }
  }

  private static org.w3c.dom.Element createElement(Document document, Element node) {
    String name = node.nodeName().toLowerCase();
    var element = document.createElement(name);

    for (var entry : node.attributes().entrySet()) {
      element.setAttribute(entry.getKey(), entry.getValue());
    }

    if (!EMPTY_ELEMENTS.contains(name)) {
      for (Node childNode : node.childNodes()) {
        var content = createContent(document, childNode);
        if (content != null) {
          element.appendChild(content);
        }
      }
    }

    return element;
  }

  private static Node createNodeFromContent(org.w3c.dom.Node node, NodeConverterContext context) {
    if (node instanceof org.w3c.dom.Text text) {
      String wholeText = text.getWholeText().trim();
      if (wholeText.isEmpty()) {
        return null;
      }
      return new Text(wholeText);
    } else if (node instanceof org.w3c.dom.Element element) {
      return createNodeFromElement(element, context);
    } else {
      if (log.isWarnEnabled()) {
        log.warn(
            "Content type '{}' is not supported. Content value '{}'.",
            node.getNodeType(),
            node.getNodeValue());
      }
      return null;
    }
  }

  private static Node createNodeFromElement(
      org.w3c.dom.Element element, NodeConverterContext context) {
    String tagName = element.getTagName().toLowerCase();
    Node node;
    if (EMPTY_ELEMENTS.contains(tagName)) {
      node = new EmptyElement(tagName);
    } else {
      if (FRAME_TAG_NAME.equals(tagName)) {
        node = createFrame(context);
      } else {
        node = new Element(tagName);
      }
      createChildNodes(element, context, node);
    }
    NamedNodeMap attributes = element.getAttributes();
    for (var i = 0; i < attributes.getLength(); i++) {
      var attribute = (Attr) attributes.item(i);
      node.attributes().put(attribute.getName(), attribute.getValue());
    }
    context.hasRoot = true;
    return node;
  }

  // unmarshaller section

  private static void createChildNodes(
      org.w3c.dom.Element element, NodeConverterContext context, Node node) {
    NodeList childNodes = element.getChildNodes();
    for (var i = 0; i < childNodes.getLength(); i++) {
      var childNode = childNodes.item(i);
      try {
        var componentFromContent = createNodeFromContent(childNode, context);
        if (componentFromContent != null) {
          node.addChild(componentFromContent);
        }
      } catch (Exception e) {
        if (log.isErrorEnabled()) {
          log.error(e.getMessage(), e);
        }
      }
    }
  }

  private static Node createFrame(NodeConverterContext context) {
    if (context.hasFrame) {
      throw new IllegalStateException(
          "Failed to parse node tree. There could be only one frame element.");
    }
    if (context.hasRoot) {
      throw new IllegalStateException(
          "Failed to parse node tree. Frame element should be the root element.");
    }
    context.hasFrame = true;
    context.frame = new Frame();
    return context.frame;
  }

  private static final class NodeConverterContext {

    private boolean hasFrame;
    private boolean hasRoot;
    private Frame frame; // used for direct access to store stylesheets directly.
  }
}
