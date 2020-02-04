package com.spinyowl.spinygui.core.converter.css.parser.visitor;

import com.spinyowl.spinygui.core.converter.css.parser.antlr.CSS3BaseVisitor;
import com.spinyowl.spinygui.core.converter.css.parser.antlr.CSS3Parser;
import com.spinyowl.spinygui.core.converter.css.Properties;
import com.spinyowl.spinygui.core.converter.css.Property;

public class PropertyVisitor extends CSS3BaseVisitor<Property> {

    @Override
    public Property visitKnownDeclaration(CSS3Parser.KnownDeclarationContext ctx) {
        return Properties.getInstance().createProperty(ctx.property().getText(), ctx.expr().getText());
    }

}
