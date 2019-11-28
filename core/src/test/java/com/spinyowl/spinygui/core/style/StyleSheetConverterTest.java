package com.spinyowl.spinygui.core.style;

import static com.spinyowl.spinygui.core.NodeBuilder.*;

import com.spinyowl.spinygui.core.api.Frame;
import com.spinyowl.spinygui.core.converter.StyleSheetConverter;
import com.spinyowl.spinygui.core.style.css.RuleSet;
import com.spinyowl.spinygui.core.style.css.StyleSheet;
import com.spinyowl.spinygui.core.converter.css.parser.StyleSheetException;
import com.spinyowl.spinygui.core.converter.NodeConverter;
import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.node.element.Button;
import com.spinyowl.spinygui.core.node.element.Label;
import com.spinyowl.spinygui.core.node.base.Node;
import com.spinyowl.spinygui.core.style.css.selector.StyleSelector;
import com.spinyowl.spinygui.core.style.manager.StyleManagerProvider;
import com.spinyowl.spinygui.core.style.types.Color;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class StyleSheetConverterTest {


    @Test
    public void createFromCSS() throws StyleSheetException {
        String css = "div > button .test" +
                "{" +
                "   background: #ffff80;" +
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

        Assert.assertFalse(selectors.get(0).test(div));
        Assert.assertTrue(selectors.get(0).test(testLabel));

        Frame frame = new Frame();
        frame.getStyleSheets().add(stylesheet);
        frame.getContainer().addChild(div);

        StyleManagerProvider.getInstance().recalculateStyles(frame);

        Assert.assertEquals(Color.RED, testLabel.getCalculatedStyle().getColor());
    }

    @Test
    public void searchComponents() throws Exception {
        var css =
                "div .test label { background: red; }" +
                        "div .test { background: green; border: 1px, 1px, 2px, 1px }" +
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
            Assert.assertEquals(Label.class, node.getClass());
        }


        Set<Element> test = StyleSheet.searchElements(ruleSets.get(1), componentTree);
        for (Element node : test) {
            Assert.assertEquals("test", node.getAttribute("class"));
        }


        Set<Element> buttons = StyleSheet.searchElements(ruleSets.get(2), componentTree);
        for (Node node : buttons) {
            Assert.assertEquals(Button.class, node.getClass());
        }

    }

    @Test
    public void parseText() throws Exception {

        var xml = "<div>just a text</div>";
        var componentTree = NodeConverter.fromXml(xml);

        System.out.println(componentTree);

    }
}