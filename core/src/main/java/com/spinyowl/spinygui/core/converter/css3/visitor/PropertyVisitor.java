package com.spinyowl.spinygui.core.converter.css3.visitor;

import com.spinyowl.spinygui.core.converter.css3.CSS3BaseVisitor;
import com.spinyowl.spinygui.core.converter.css3.CSS3Parser;
import com.spinyowl.spinygui.core.style.Property;
import com.spinyowl.spinygui.core.style.types.Color;

public class PropertyVisitor extends CSS3BaseVisitor<Property> {

    @Override
    public Property visitKnownDeclaration(CSS3Parser.KnownDeclarationContext ctx) {
        var name = ctx.property().getText();
        var value = new ValueVisitor().visit(ctx.expr());
        if (value == null)
            value = parseValue(name, ctx.expr().getText());
        var declaration = new Property(name, value);

        return declaration;
    }

    private Object parseValue(String name, String value) {
        if (value.equals("none"))
            return null;
        switch (name) {
            case "background":
            case "color":
                return Color.getColorByName(value);
        }
        return null;
    }

    private Color parseColor(String value) {

        /*if (value.matches("#[0-9a-fA-F]{3}")) {
            int hex = Integer.parseInt(value.substring(1), 16);
            int r = (hex >> 2) & 0xF;
            int g = (hex >> 1) & 0xF;
            int b = hex & 0xF;
            r |= r << 1;
            g |= r << 1;
            b |= r << 1;
            return new Color(r, g, b);
        }
        if (value.matches("#[0-9a-fA-F]{6}")) {
            int hex = Integer.parseInt(value.substring(1), 16);
            int r = (hex >> 4) & 0xFF;
            int g = (hex >> 2) & 0xFF;
            int b = hex & 0xFF;
            return new Color(r, g, b);
        }*/
        return null;
    }
}
