package com.spinyowl.spinygui.core.parser.impl.css.parser.visitor;

import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3BaseVisitor;
import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3Parser.KnownDeclarationContext;
import com.spinyowl.spinygui.core.style.stylesheet.Declaration;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyStore;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class PropertyVisitor extends CSS3BaseVisitor<Declaration> {

  @NonNull private final PropertyStore propertyStore;

  @Override
  public Declaration visitKnownDeclaration(KnownDeclarationContext ctx) {
    String propertyName = ctx.property().getText();
    Property property = propertyStore.getProperty(propertyName);
    String value = ctx.expr().getText();
    if (property != null && value != null) {
      return new Declaration(property, value);
    }
//    log.warn("Can't parse {} property with {} value", propertyName, value);
    return null;
  }
}
