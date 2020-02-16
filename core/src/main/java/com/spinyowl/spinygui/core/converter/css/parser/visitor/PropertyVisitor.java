package com.spinyowl.spinygui.core.converter.css.parser.visitor;

import com.spinyowl.spinygui.core.converter.css.Properties;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.converter.css.Rule;
import com.spinyowl.spinygui.core.converter.css.parser.antlr.CSS3BaseVisitor;
import com.spinyowl.spinygui.core.converter.css.parser.antlr.CSS3Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyVisitor extends CSS3BaseVisitor<Rule> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyVisitor.class);

    @Override
    public Rule visitKnownDeclaration(CSS3Parser.KnownDeclarationContext ctx) {
        String propertyName = ctx.property().getText();
        Property property = Properties.getProperty(propertyName);
        String value = ctx.expr().getText();
        if (property != null && value != null) {
            return new Rule(property, value);
        }
        LOGGER.warn("Can't parse {} property with {} value", propertyName, value);
        return null;
    }

}
