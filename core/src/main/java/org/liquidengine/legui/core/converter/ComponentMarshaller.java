package org.liquidengine.legui.core.converter;

import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Text;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.liquidengine.legui.core.component.base.ComponentBase;
import org.liquidengine.legui.core.component.base.ComponentMapping;
import org.liquidengine.legui.core.component.base.TextComponent;

import java.io.StringReader;

public class ComponentMarshaller {
//    private static final String OPEN_TAG_START = "<";
//    private static final String OPEN_TAG_END = ">";
//    private static final String OPEN_EMPTY_TAG_END = "/>";
//    private static final String CLOSE_TAG_START = "</";
//    private static final String CLOSE_TAG_END = ">";
//    private static final String SPACE = " ";
//    private static final String QUOTE = "\"";
//    private static final String EQUAL = "=";
//
//
//    public static String marshal(ComponentBase node) {
//        if (node == null) return null;
//        StringBuilder xml = new StringBuilder();
//        appendChildNode(node, xml);
//        return xml.toString();
//    }
//
//    private static void appendChildNode(ComponentBase node, StringBuilder xml) {
//        if (node instanceof Component) {
//            appendChildElement((Component) node, xml);
//        } else if (node instanceof TextComponent) {
//            xml.append(((TextComponent) node).getText());
//        }
//    }
//
//    private static void appendChildElement(Component element, StringBuilder b) {
//        String tagName = getName(element);
//
//        // open tag
//        b.append(OPEN_TAG_START).append(tagName);
//
//        // append attributes
//        Map<String, String> attributes = element.getAttributes();
//        Set<Map.Entry<String, String>> entries = attributes.entrySet();
//        for (Map.Entry<String, String> entry : entries) {
//            String val = entry.getValue()
//                    .replace("\"", "&quot;")
//                    .replace("<", "&lt;")
//                    .replace("&", "&amp;")
//                    .replace(">", "&gt;");
//            b.append(SPACE).append(entry.getKey()).append(EQUAL).append(QUOTE).append(val).append(QUOTE);
//        }
//
//
//        if (element.getChildBaseComponents().isEmpty() || element instanceof EmptyComponent) { // close
//            b.append(OPEN_EMPTY_TAG_END);
//        } else { // add body and close
//            b.append(OPEN_TAG_END);
//            List<ComponentBase> childNodes = element.getChildBaseComponents();
//            for (ComponentBase childNode : childNodes) {
//                appendChildNode(childNode, b);
//            }
//            b.append(CLOSE_TAG_START).append(tagName).append(CLOSE_TAG_END);
//        }
//    }
//
//    private static String getName(Component element) {
//        return element.getClass().getSimpleName().toLowerCase();
//    }
//
//    private static String textNodeToString(TextComponent node) {
//        return node.getText();
//    }

    public static String marshal(ComponentBase component) {
        if (component == null) {
            return null;
        }

        Document document = new Document();
        Content content = createContent(component);
        document.addContent(content);

        XMLOutputter out = new XMLOutputter();
        out.setFormat(Format.getPrettyFormat());
        return out.outputString(document);
    }

    private static Content createContent(ComponentBase component) {
        if (component instanceof TextComponent) {
            return new Text(((TextComponent) component).getText());
        } else {
            return createElement(component);
        }
    }

    private static Element createElement(ComponentBase component) {
        Element element = new Element(getTagName(component));
        for (var entry : component.getAttributes().entrySet()) {
            element.setAttribute(entry.getKey(), entry.getValue());
        }
        for (ComponentBase childBaseComponent : component.getChildBaseComponents()) {
            element.addContent(createContent(childBaseComponent));
        }
        return element;
    }

    private static <T extends ComponentBase> String getTagName(T component) {
        var componentClass = component.getClass();
        if (ComponentMapping.getTagMapping().containsKey(componentClass)) {
            return ComponentMapping.getTagMapping().get(componentClass);
        } else {
            return componentClass.getCanonicalName();
        }
    }

    public static ComponentBase unmarshal(String xml) throws Exception {
        if (xml == null || xml.isEmpty())
            return null;
        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(new StringReader(xml));
        Element element = document.getRootElement();
        ComponentBase component = createComponentFromContent(element);
        return component;
    }

    private static ComponentBase createComponentFromContent(Content element) throws Exception {
        if (element instanceof Text) {
            return new TextComponent(((Text) element).getText());
        } else if (element instanceof Element) {
            return createComponentFromElement((Element) element);
        } else return null;
    }

    private static ComponentBase createComponentFromElement(Element element) throws Exception {
        ComponentBase instance = getClassByTag(element.getName()).getDeclaredConstructor().newInstance();
        element.getAttributes().forEach(a -> instance.setAttribute(a.getName(), a.getValue()));

        for (Content c : element.getContent()) {
            try {
                ComponentBase componentFromContent = createComponentFromContent(c);
                if (componentFromContent != null) {
                    instance.addChild(componentFromContent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return instance;
    }

    private static Class<? extends ComponentBase> getClassByTag(String name) throws Exception {
        if (ComponentMapping.getTagMapping().containsValue(name)) {
            return ComponentMapping.getTagMapping().inverse().get(name);
        }
        return (Class<? extends ComponentBase>) Class.forName(name);
    }
}
