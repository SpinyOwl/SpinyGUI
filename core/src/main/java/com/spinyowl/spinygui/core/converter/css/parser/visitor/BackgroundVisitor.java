package com.spinyowl.spinygui.core.converter.css.parser.visitor;

import com.spinyowl.spinygui.core.converter.css.parser.CSS3BaseVisitor;
import com.spinyowl.spinygui.core.converter.css.parser.CSS3Parser;
import com.spinyowl.spinygui.core.style.types.Background;
import com.spinyowl.spinygui.core.style.types.Color;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BackgroundVisitor extends CSS3BaseVisitor<Background> {
    private static final Log LOGGER = LogFactory.getLog(BackgroundVisitor.class);

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean shouldVisitNextChild(RuleNode node, Background currentResult) {
        return currentResult == null;
    }

    @Override
    public Background visitHexcolor(CSS3Parser.HexcolorContext ctx) {
        Background background = new Background();

        String value = ctx.getText();

        Color color = null;
        String hexPattern = "^#([A-Fa-f0-9]{8}|[A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";
        Matcher matcher = Pattern.compile(hexPattern).matcher(value);
        if (matcher.find()) {
            color =  Color.parseHexString(value.substring(1));
        }

        background.setColor(color);
        return background;
    }

    @Override
    public Background visitFunction(CSS3Parser.FunctionContext ctx) {
        Background background = new Background();
        String name = ctx.Function().getText().substring(0, ctx.Function().getText().length() - 1);

        Color color = null;
        if (name.equals("rgb"))
            color = Color.parseColorString(ctx.expr().getText());
        if (name.equals("rgba"))
            color = Color.parseColorString(ctx.expr().getText());

        background.setColor(color);

        return background;
    }

    @Override
    public Background visitTerminal(TerminalNode node) {
        Background background = new Background();
        background.setColor(Color.getColorByName(node.getText()));
        return background;
    }

}
