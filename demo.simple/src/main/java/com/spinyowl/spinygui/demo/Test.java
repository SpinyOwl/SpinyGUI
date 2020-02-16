package com.spinyowl.spinygui.demo;

import com.spinyowl.spinygui.core.api.Frame;
import com.spinyowl.spinygui.core.converter.NodeConverter;
import com.spinyowl.spinygui.core.converter.StyleSheetConverter;
import com.spinyowl.spinygui.core.converter.css.RuleSet;
import com.spinyowl.spinygui.core.converter.css.StyleSheet;
import com.spinyowl.spinygui.core.converter.css.parser.StyleReflectionHandler;
import com.spinyowl.spinygui.core.converter.css.parser.StyleSheetException;
import com.spinyowl.spinygui.core.converter.css.selector.StyleSelector;
import com.spinyowl.spinygui.core.converter.dom.TagNameMapping;
import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.node.base.Node;
import com.spinyowl.spinygui.core.node.element.Button;
import com.spinyowl.spinygui.core.node.element.Label;
import com.spinyowl.spinygui.core.style.manager.StyleManagerProvider;
import com.spinyowl.spinygui.core.style.types.Color;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.spinyowl.spinygui.core.NodeBuilder.*;

public class Test {
    public static void main(String[] args) throws Exception {
        createFromCSS();
        searchComponents();
        parseText();
    }

    public static void createFromCSS() throws StyleSheetException {
        StyleReflectionHandler.getPseudoSelector(":hover");
        Class<? extends Element> aClass = TagNameMapping.getElement("div");
        String css = "div > button .test" +
                "{" +
                "   background-color: #ffff80;" +
                "   color: red;" +
                "   width: 50%;" +
                "   top: 50%;" +
                "   top: 50px;" +
                "   top: 50.2%;" +
                "}";

        var stylesheet = StyleSheetConverter.createFromCSS(css);

        Label testLabel = label(Map.of("class", "test"));

        Element div = div(button(testLabel));

        List<RuleSet> ruleSets = stylesheet.getRuleSets();
        RuleSet ruleSet = ruleSets.get(0);
        List<StyleSelector> selectors = ruleSet.getSelectors();

        assert (!selectors.get(0).test(div));
        assert (selectors.get(0).test(testLabel));

        Frame frame = new Frame();
        frame.getStyleSheets().add(stylesheet);
        frame.getDefaultLayer().addChild(div);

        StyleManagerProvider.getInstance().recalculateStyles(frame);

        assert (Objects.equals(Color.RED, testLabel.getCalculatedStyle().getColor()));
    }

    public static void searchComponents() throws Exception {
        var css =
                "div .test label { background-color: red; }" +
                        "div .test { background-color: green; border: 1px, 1px, 2px, 1px }" +
                        "button { color: black; }";

        var stylesheet = StyleSheetConverter.createFromCSS(css);

        var xml = "<div id=\"1\">\n" +
                "    <div id=\"2\" class=\"test\">\n" +
                "        <label id=\"3\">Label 1</label>\n" +
                "    </div>\n" +
                "    <button id=\"4\" class=\"test\"/>\n" +
                "    <div id=\"5\" class=\"test\">\n" +
                "        <div id=\"6\">\n" +
                "            <div id=\"7\" class=\"test\">\n" +
                "                <label id=\"8\">Label 1</label>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</div>";
        var componentTree = (Element) NodeConverter.fromXml(xml);

        List<RuleSet> ruleSets = stylesheet.getRuleSets();

        Set<Element> labels = StyleSheet.searchElements(ruleSets.get(0), componentTree);
        for (Node node : labels) {
            assert (Objects.equals(Label.class, node.getClass()));
        }
        Set<Element> test = StyleSheet.searchElements(ruleSets.get(1), componentTree);
        for (Element node : test) {
            assert (Objects.equals("test", node.getAttribute("class")));
        }
        Set<Element> buttons = StyleSheet.searchElements(ruleSets.get(2), componentTree);
        for (Node node : buttons) {
            assert (Objects.equals(Button.class, node.getClass()));
        }

    }

    public static void parseText() throws Exception {

        var xml = "<div>just a text</div>";
        var componentTree = NodeConverter.fromXml(xml);

        System.out.println(componentTree);

    }
}
