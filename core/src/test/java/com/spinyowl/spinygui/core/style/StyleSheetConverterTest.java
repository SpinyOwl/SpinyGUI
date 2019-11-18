package com.spinyowl.spinygui.core.style;

import static com.spinyowl.spinygui.core.NodeBuilder.*;

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
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Set;

public class StyleSheetConverterTest {


    @Test
    public void createFromCSS() throws StyleSheetException {
        String css = "panel > button .test" +
                "{" +
                "   background: #ffff80;" +
                "   color: red;" +
                "   width: 50%;" +
                "   top: 50%;" +
                "   top: 50px;" +
                "   top: 50.2;" +
                "}";

        var stylesheet = StyleSheetConverter.createFromCSS(css);

        Label testLabel = label();
        testLabel.setAttribute("class", "test");
        Node p = panel(button(testLabel));
        List<RuleSet> ruleSets = stylesheet.getRuleSets();
        RuleSet ruleSet = ruleSets.get(0);
        List<StyleSelector> selectors = ruleSet.getSelectors();

        Assert.assertFalse(selectors.get(0).test(p));
        Assert.assertTrue(selectors.get(0).test(testLabel));
    }

    @Test
    public void searchComponents() throws Exception {
        var css =
                "panel .test label { background: red; }" +
                        "panel .test { background: green; border: 1px, 1px, 2px, 1px }" +
                        "button { color: black; }";

        var stylesheet = StyleSheetConverter.createFromCSS(css);

        var xml = "<panel id=\"1\">\n" +
                "    <panel id=\"2\" class=\"test\">\n" +
                "        <label id=\"3\">Label 1</label>\n" +
                "    </panel>\n" +
                "    <button id=\"4\" class=\"test\"/>\n" +
                "    <panel id=\"5\" class=\"test\">\n" +
                "        <panel id=\"6\">\n" +
                "            <panel id=\"7\" class=\"test\">\n" +
                "                <label id=\"8\">Label 1</label>\n" +
                "            </panel>\n" +
                "        </panel>\n" +
                "    </panel>\n" +
                "</panel>";
        var componentTree = (Element) NodeConverter.fromXml(xml);

        List<RuleSet> ruleSets = stylesheet.getRuleSets();

        Set<Element> labels = StyleSheet.searchElements(ruleSets.get(0), componentTree);
        for (Node node : labels) {
            Assert.assertEquals(Label.class, node.getClass());
        }


        Set<Element> test = StyleSheet.searchElements(ruleSets.get(1), componentTree);
        for (Node node : test) {
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