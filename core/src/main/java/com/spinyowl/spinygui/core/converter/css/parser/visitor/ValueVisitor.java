package com.spinyowl.spinygui.core.converter.css.parser.visitor;

import com.spinyowl.spinygui.core.converter.css.parser.CSS3BaseVisitor;
import com.spinyowl.spinygui.core.converter.css.parser.CSS3Parser;

public class ValueVisitor extends CSS3BaseVisitor<Object> {

    @Override
    public Object visitNumber(CSS3Parser.NumberContext ctx) {
        String value = ctx.getText();

        if (value.contains(".")) {
            return Float.parseFloat(value);
        }
        return Integer.parseInt(value);
    }

    @Override
    public Object visitPercentage(CSS3Parser.PercentageContext ctx) {
        String value = ctx.getText();

        return super.visitPercentage(ctx);
    }


    @Override
    public Object visitIdent(CSS3Parser.IdentContext ctx) {
        return super.visitIdent(ctx);
    }
}
