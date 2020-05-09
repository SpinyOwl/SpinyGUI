package com.spinyowl.spinygui.core.converter;

import com.spinyowl.spinygui.core.converter.dom.RawProcessor;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.EmptyElement;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.Text;
import java.io.StringReader;
import java.util.List;
import java.util.stream.Collectors;
import javax.xml.XMLConstants;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

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

    Document document = new Document();
    Content content = createContent(node);
    document.addContent(content);

    XMLOutputter out = new XMLOutputter(new RawProcessor());
    out.setFormat(pretty ? Format.getPrettyFormat() : Format.getRawFormat());
    return out.outputString(document);
  }

  private static Content createContent(Node node) {
    if (node instanceof Text) {
      return new org.jdom2.Text(((Text) node).content());
    } else if (node instanceof Element) {
      return createElement((Element) node);
    } else {
      log.warn("Attempt to marshal {} class which is not Text or Element child -> skipping.",
          node.getClass().getName());
      return null;
    }
  }

  private static org.jdom2.Element createElement(Element node) {
    String name = node.nodeName().toLowerCase();
    org.jdom2.Element element = new org.jdom2.Element(name);

    for (var entry : node.attributes().entrySet()) {
      element.setAttribute(entry.getKey(), entry.getValue());
    }

    if (!EMPTY_ELEMENTS.contains(name)) {
      for (Node childNode : node.childNodes()) {
        Content content = createContent(childNode);
        if (content != null) {
          element.addContent(content);
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
    SAXBuilder saxBuilder = new SAXBuilder(); // Compliant
    saxBuilder.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, ""); // Compliant
    saxBuilder.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, ""); // Compliant
    return createNodeFromContent(saxBuilder.build(new StringReader(xml)).getRootElement());
  }

  private static Node createNodeFromContent(Content content) {
    if (content instanceof org.jdom2.Text) {
      org.jdom2.Text text = (org.jdom2.Text) content;
      if (text.getTextTrim().isEmpty()) {
        return null;
      }
      return new Text(text.getText());
    } else if (content instanceof org.jdom2.Element) {
      return createNodeFromElement((org.jdom2.Element) content);
    } else {
      log.warn("Content type '{}' is not supported. Content value '{}'.",
          content.getCType(), content.getValue());
      return null;
    }
  }

  private static Node createNodeFromElement(org.jdom2.Element element) {
    String tagName = element.getName().toLowerCase();
    Node instance;
    if (EMPTY_ELEMENTS.contains(tagName)) {
      instance = new EmptyElement(tagName);
    } else {
      instance = new Element(tagName);
      for (Content c : element.getContent()) {
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
    instance.attributes().putAll(element.getAttributes().stream()
        .collect(Collectors.toMap(Attribute::getName, Attribute::getValue)));

    return instance;
  }
}
