package com.spinyowl.spinygui.core.converter.css3.visitor;

import com.spinyowl.spinygui.core.converter.css3.CSS3BaseVisitor;
import com.spinyowl.spinygui.core.converter.css3.CSS3Parser;
import com.spinyowl.spinygui.core.style.types.Color;

public class ValueVisitor extends CSS3BaseVisitor<Object> {

    @Override
    public Object visitHexcolor(CSS3Parser.HexcolorContext ctx) {
        String value = ctx.getText();

        if (value.length() == 7) {  // #ffffff
            int hex = Integer.parseInt(value.substring(1), 16);
            int r = (hex >> 16) & 0xFF;
            int g = (hex >> 8) & 0xFF;
            int b = hex & 0xFF;
            return new Color(r, g, b);
        }

        if (value.length() == 4) { // #fff
            int hex = Integer.parseInt(value.substring(1), 16);
            int r = (hex >> 2) & 0xF;
            int g = (hex >> 1) & 0xF;
            int b = hex & 0xF;
            r |= r << 4;
            g |= g << 4;
            b |= b << 4;
            return new Color(r, g, b);
        }

        return null;
    }

    @Override
    public Object visitFunction(CSS3Parser.FunctionContext ctx) {
        String name = ctx.Function().getText().substring(0, ctx.Function().getText().length() - 1);

        if (name.equals("rgb"))
            return parseRGB(ctx);
        if (name.equals("rgba"))
            return parseRGB(ctx);

        return super.visitFunction(ctx);
    }

    private Object parseRGB(CSS3Parser.FunctionContext ctx) {
        var values = ctx.expr().getText().split(",");
        if (values.length == 3) {
            try {
                int r = Integer.parseInt(values[0].trim());
                int g = Integer.parseInt(values[1].trim());
                int b = Integer.parseInt(values[2].trim());
                return new Color(r, g, b);
            } catch (Exception e) {
                e.printStackTrace();
                //TODO proper exception handling
            }
        }

        return null;
    }

    private Object parseRGBA(CSS3Parser.FunctionContext ctx) {
        var values = ctx.expr().getText().split(",");
        if (values.length == 4) {
            try {
                int r = Integer.parseInt(values[0].trim());
                int g = Integer.parseInt(values[1].trim());
                int b = Integer.parseInt(values[2].trim());
                float a = Float.parseFloat(values[3].trim());
                return new Color(r, g, b, a);
            } catch (Exception e) {
                e.printStackTrace();
                //TODO proper exception handling
            }
        }

        return null;
    }

    @Override
    public Object visitNumber(CSS3Parser.NumberContext ctx) {
        String value = ctx.getText();
        if (value.contains(".")) {
            return Float.parseFloat(value);
        }
        return Integer.parseInt(value);
    }

}
