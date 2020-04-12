package com.spinyowl.spinygui.core.converter.css.parser.visitor;

import com.spinyowl.spinygui.core.converter.css.Declaration;
import com.spinyowl.spinygui.core.converter.css.Properties;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.converter.css.parser.antlr.CSS3BaseVisitor;
import com.spinyowl.spinygui.core.converter.css.parser.antlr.CSS3Parser;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PropertyVisitor extends CSS3BaseVisitor<Declaration> {

    @Override
    public Declaration visitKnownDeclaration(CSS3Parser.KnownDeclarationContext ctx) {
        String propertyName = ctx.property().getText();
        Property property = Properties.getProperty(propertyName);
        String value = ctx.expr().getText();
        if (property != null && value != null) {
            return new Declaration(property, value);
        }
        LOGGER.warn("Can't parse {} property with {} value", propertyName, value);
        return null;
    }

}
