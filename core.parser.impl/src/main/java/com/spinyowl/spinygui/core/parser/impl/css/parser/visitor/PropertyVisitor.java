package com.spinyowl.spinygui.core.parser.impl.css.parser.visitor;

import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3BaseVisitor;
import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3Parser.KnownDeclarationContext;
import com.spinyowl.spinygui.core.style.stylesheet.Declaration;
import com.spinyowl.spinygui.core.style.stylesheet.Properties;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PropertyVisitor extends CSS3BaseVisitor<Declaration<?>> {

  @Override
  public Declaration<?> visitKnownDeclaration(KnownDeclarationContext ctx) {
    String propertyName = ctx.property().getText();
    Property<?> property = Properties.getProperty(propertyName);
    String value = ctx.expr().getText();
    if (property != null && value != null) {
      return new Declaration<>(property, value);
    }
    log.warn("Can't parse {} property with {} value", propertyName, value);
    return null;
  }

}
