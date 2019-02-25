package com.spinyowl.spinygui.core.style;

import com.spinyowl.spinygui.core.Configuration;
import com.spinyowl.spinygui.core.component.Button;
import com.spinyowl.spinygui.core.component.Label;
import com.spinyowl.spinygui.core.component.Panel;
import com.spinyowl.spinygui.core.component.base.Component;
import com.spinyowl.spinygui.core.converter.css3.StyleSheetException;
import com.spinyowl.spinygui.core.style.selector.StyleSelector;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

public class StyleSheetFactoryTest {

    @BeforeClass
    public static void setupClass() {
        Configuration.SERVICE_PROVIDER.setValue(TestServiceProvider.class.getName());
    }

    @Test
    public void createFromCSS() throws StyleSheetException {
        String css = "panel > button .test" +
                "{" +
                "   background: #ffff80;" +
                "   color: red;" +
                "}";

        var stylesheet = StyleSheetFactory.createFromCSS(css);

        Label testLabel = new Label();
        testLabel.setClassAttribute("test");
        Component p = new Panel().add(new Button().add(testLabel));
        List<RuleSet> ruleSets = stylesheet.getRuleSets();
        RuleSet ruleSet = ruleSets.get(0);
        List<StyleSelector> selectors = ruleSet.getSelectors();

        Assert.assertFalse(selectors.get(0).test(p));
        Assert.assertTrue(selectors.get(0).test(testLabel));

        System.out.println(stylesheet);
    }
}