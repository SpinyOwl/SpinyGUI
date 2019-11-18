package com.spinyowl.spinygui.core.converter.css.parser.visitor;

import com.spinyowl.spinygui.core.converter.css.parser.CSS3BaseVisitor;
import com.spinyowl.spinygui.core.converter.css.parser.CSS3Parser;
import com.spinyowl.spinygui.core.converter.css.parser.PropertyVisitors;
import com.spinyowl.spinygui.core.style.css.n.Properties;
import com.spinyowl.spinygui.core.style.css.n.Property;

public class PropertyVisitor extends CSS3BaseVisitor<Property> {

    @Override
    public Property visitKnownDeclaration(CSS3Parser.KnownDeclarationContext ctx) {
        var name = ctx.property().getText();
        CSS3BaseVisitor visitor = PropertyVisitors.getInstance().getVisitorFor(name);
        if (visitor != null) {
            Object value = visitor.visit(ctx.expr());
//            Properties.getInstance().valueOf(name, value);
            return new Property(name, null);
        } else {
            return super.visitKnownDeclaration(ctx);
        }
//        var name = ctx.property().getText();
//        var value = new ValueVisitor().visit(ctx.expr());
//        if (value == null)
//            value = parseValue(name, ctx.expr().getText());
//        return new Property(name, value);
//    }
//
//    private Object parseValue(String name, String value) {
//        if (value.equals("none"))
//            return null;
//        if ("background".equals(name) || "color".equals(name)) {
//            return Color.getColorByName(value);
//        }
//        return null;
    }

}
