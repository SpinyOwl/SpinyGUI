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
import java.io.StringWriter;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

@Slf4j
public class DefaultNodeParser implements NodeParser {

  private static final List<String> EMPTY_ELEMENTS =
      List.of(
          "area", "base", "br", "col", "embed", "hr", "img", "input", "keygen", "link", "meta",
          "param", "source", "track", "wbr");
  private static final String INDENT_AMOUNT = "{http://xml.apache.org/xslt}indent-amount";

  @Override
  public Node fromHtml(String html) {
    if (html == null || html.isBlank()) {
      return null;
    }

    Document document = Jsoup.parse(html);

    Elements frameElements = document.getElementsByTag(FRAME_TAG_NAME);
    if (frameElements.size() == 1) {
      return createNodeFromElement(frameElements.getFirst(), new NodeConverterContext());
    }
    var bodyChildren = document.body().children();
    if (bodyChildren.size() == 1) {
      return createNodeFromElement(bodyChildren.getFirst(), new NodeConverterContext());
    }
    return createNodeFromElement(document.body(), new NodeConverterContext());
  }

  private Node createNodeFromContent(org.jsoup.nodes.Node node, NodeConverterContext context) {
    if (node instanceof org.jsoup.nodes.Element element) {
      return createNodeFromElement(element, context);
    } else if (node instanceof org.jsoup.nodes.TextNode text) {
      String wholeText = text.getWholeText();
      if (wholeText.isBlank()) {
        return null;
      }
      return new Text(wholeText);
    } else {
      if (log.isWarnEnabled()) {
        log.warn(
            "Content type '{}' is not supported. Content value: {}.",
            node.nodeName(),
            node.outerHtml());
      }
      return null;
    }
  }

  private Node createNodeFromElement(
      org.jsoup.nodes.Element element, NodeConverterContext context) {
    String tagName = element.tagName().toLowerCase();
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
    var attributes = element.attributes().asList();
    for (var i = 0; i < attributes.size(); i++) {
      var attribute = attributes.get(i);
      node.attributes().put(attribute.getKey(), attribute.getValue());
    }
    context.hasRoot = true;
    return node;
  }

  // unmarshaller section

  private void createChildNodes(
      org.jsoup.nodes.Element element, NodeConverterContext context, Node node) {
    var childNodes = element.childNodes();
    for (org.jsoup.nodes.Node childNode : childNodes) {
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

  private Node createFrame(NodeConverterContext context) {
    if (context.hasFrame) {
      throw new IllegalStateException(
          "Failed to parse node tree. There could be only one frame element.");
    }
    if (context.hasRoot) {
      throw new IllegalStateException(
          "Failed to parse node tree. Frame element could be only the root element.");
    }
    context.hasFrame = true;
    context.frame = new Frame();
    return context.frame;
  }

  @Override
  public String toHtml(Node node) {
    return toHtml(node, true);
  }

  @Override
  public String toHtml(Node node, boolean pretty) {
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

  private org.w3c.dom.Node createContent(org.w3c.dom.Document document, Node node) {
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

  private org.w3c.dom.Element createElement(org.w3c.dom.Document document, Element node) {
    String name = node.nodeName().toLowerCase();
    log.debug("Creating element with name: {}", name);
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

  private static class NodeConverterContext {
    private boolean hasFrame;
    private boolean hasRoot;
    private Frame frame;
  }
}
