package com.spinyowl.spinygui.core.converter;

import com.spinyowl.spinygui.core.converter.dom.RawProcessor;
import com.spinyowl.spinygui.core.converter.dom.TagNameMapping;
import com.spinyowl.spinygui.core.node.base.Node;
import com.spinyowl.spinygui.core.node.base.Text;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.StringReader;

/**
 * Node marshaller. Used to convert node to xml and vise versa.
 * Uses {@link TagNameMapping} to find out tag name or if there is no mapping - uses class canonical name
 */
public class NodeConverter {
    private static final Log LOGGER = LogFactory.getLog(NodeConverter.class);

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
            return new org.jdom2.Text(((Text) node).getText());
        } else {
            return createElement(node);
        }
    }

    private static Element createElement(Node node) {
        Element element = new Element(getTagName(node));
        if (node instanceof com.spinyowl.spinygui.core.node.base.Element) {
            com.spinyowl.spinygui.core.node.base.Element e = (com.spinyowl.spinygui.core.node.base.Element) node;

            for (var entry : e.getAttributes().entrySet()) {
                element.setAttribute(entry.getKey(), entry.getValue());
            }
            for (Node childNode : e.getChildNodes()) {
                element.addContent(createContent(childNode));
            }
        }
        return element;
    }

    private static <T extends Node> String getTagName(T component) {
        var componentClass = component.getClass();
        if (TagNameMapping.containsKey(componentClass)) {
            return TagNameMapping.get(componentClass);
        } else {
            return componentClass.getCanonicalName();
        }
    }

    // unmarshaller section

    public static Node fromXml(String xml) throws Exception {
        if (xml == null || xml.isEmpty()) {
            return null;
        }
        return createNodeFromContent(new SAXBuilder().build(new StringReader(xml)).getRootElement());
    }

    private static Node createNodeFromContent(Content content) throws Exception {
        if (content instanceof org.jdom2.Text) {
            org.jdom2.Text text = (org.jdom2.Text) content;
            if (text.getTextTrim().isEmpty()) {
                return null;
            }
            return new Text(text.getText());
        } else if (content instanceof Element) {
            return createNodeFromElement((Element) content);
        } else {
            LOGGER.warn(String.format(
                    "Can't find node mapping and class for content type '%s', content value '%s'.",
                    content.getCType(),
                    content.getValue()
            ));
            return null;
        }
    }

    private static Node createNodeFromElement(Element element) throws Exception {
        Class<? extends Node> aClass = getClassByTag(element.getName());
        if (aClass == null) {
            return null;
        }
        Node instance = aClass.getDeclaredConstructor().newInstance();
        if (instance instanceof com.spinyowl.spinygui.core.node.base.Element) {
            com.spinyowl.spinygui.core.node.base.Element el = (com.spinyowl.spinygui.core.node.base.Element) instance;
            element.getAttributes().forEach(a -> el.setAttribute(a.getName(), a.getValue()));

            for (Content c : element.getContent()) {
                try {
                    Node componentFromContent = createNodeFromContent(c);
                    if (componentFromContent != null) {
                        el.addChild(componentFromContent);
                    }
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }

        return instance;
    }

    private static Class<? extends Node> getClassByTag(String name) {
        if (TagNameMapping.containsTag(name)) {
            return TagNameMapping.getByTag(name);
        }
        try {
            return (Class<? extends Node>) Class.forName(name);
        } catch (ClassNotFoundException e) {
            LOGGER.warn(String.format("Can't find node mapping and class for tag '%s'.", name));
            return null;
        }
    }
}
