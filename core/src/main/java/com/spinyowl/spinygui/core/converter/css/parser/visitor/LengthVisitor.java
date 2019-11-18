package com.spinyowl.spinygui.core.converter.css.parser.visitor;

import com.spinyowl.spinygui.core.converter.css.parser.CSS3BaseVisitor;
import com.spinyowl.spinygui.core.converter.css.parser.CSS3Parser;
import com.spinyowl.spinygui.core.style.types.length.Length;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LengthVisitor extends CSS3BaseVisitor<Length> {
    private static final Log LOGGER = LogFactory.getLog(LengthVisitor.class);

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean shouldVisitNextChild(RuleNode node, Length currentResult) {
        return currentResult == null;
    }

    @Override
    public Length visitNumber(CSS3Parser.NumberContext ctx) {
        String value = ctx.getText();
        if (value.contains(".")) {
            return Length.pixel(Float.parseFloat(value));
        }
        return Length.pixel(Integer.parseInt(value));
    }

    @Override
    public Length visitPercentage(CSS3Parser.PercentageContext ctx) {
        return Length.percent(getFloatValue(ctx.getText()));
    }

    @Override
    public Length visitTerminal(TerminalNode node) {
        String textValue = node.getText();
        float value = getFloatValue(textValue);
        return Length.pixel(value);
    }

    private float getFloatValue(String textValue) {
        Matcher matcher = Pattern.compile("\\d+\\.?\\d*").matcher(textValue);
        if (matcher.find())
            return Float.parseFloat(matcher.group());
        return 0;
    }
}
