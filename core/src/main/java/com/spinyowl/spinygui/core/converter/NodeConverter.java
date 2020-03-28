package com.spinyowl.spinygui.core.converter;

import com.spinyowl.spinygui.core.converter.dom.RawProcessor;
import com.spinyowl.spinygui.core.converter.dom.TagNameMapping;
import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.node.base.Node;
import com.spinyowl.spinygui.core.node.base.Text;
import java.io.StringReader;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Node marshaller. Used to convert node to xml and vise versa. Uses {@link TagNameMapping} to find
 * out tag name or if there is no mapping - uses class canonical name
 */
public final class NodeConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(NodeConverter.class);

    private NodeConverter() {
    }

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
            return new org.jdom2.Text(((Text) node).getContent());
        } else if (node instanceof Element) {
            return createElement((Element) node);
        } else {
            LOGGER
                .warn("Attempt to marshal {} class which is not Text or Element child -> skipping.",
                    node.getClass().getName());
            return null;
        }
    }

    private static org.jdom2.Element createElement(Element node) {
        org.jdom2.Element element = new org.jdom2.Element(getTagName(node));

        for (var entry : node.getAttributes().entrySet()) {
            element.setAttribute(entry.getKey(), entry.getValue());
        }
        for (Node childNode : node.getChildNodes()) {
            Content content = createContent(childNode);
            if (content != null) {
                element.addContent(content);
            }
        }
        return element;
    }

    private static <T extends Element> String getTagName(T component) {
        var componentClass = component.getClass();
        if (TagNameMapping.containsElement(componentClass)) {
            return TagNameMapping.getTagName(componentClass);
        } else {
            return componentClass.getCanonicalName();
        }
    }

    // unmarshaller section

    public static Node fromXml(String xml) throws Exception {
        if (xml == null || xml.isEmpty()) {
            return null;
        }
        SAXBuilder saxBuilder = new SAXBuilder();
        return createNodeFromContent(saxBuilder.build(new StringReader(xml)).getRootElement());
    }

    private static Node createNodeFromContent(Content content) throws Exception {
        if (content instanceof org.jdom2.Text) {
            org.jdom2.Text text = (org.jdom2.Text) content;
            if (text.getTextTrim().isEmpty()) {
                return null;
            }
            return new Text(text.getText());
        } else if (content instanceof org.jdom2.Element) {
            return createNodeFromElement((org.jdom2.Element) content);
        } else {
            LOGGER.warn(
                "Can't find node mapping and class for content type '{}', content value '{}'.",
                content.getCType(), content.getValue());
            return null;
        }
    }

    private static Node createNodeFromElement(org.jdom2.Element element) throws Exception {
        Class<? extends Node> aClass = getClassByTag(element.getName());
        if (aClass == null) {
            return null;
        }
        Node instance = aClass.getDeclaredConstructor().newInstance();
        if (instance instanceof Element) {
            Element el = (Element) instance;
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
            return TagNameMapping.getElement(name);
        }
        try {
            return (Class<? extends Node>) Class.forName(name);
        } catch (ClassNotFoundException e) {
            LOGGER.warn("Can't find node mapping and class for tag '{}'.", name);
            return null;
        }
    }
}
