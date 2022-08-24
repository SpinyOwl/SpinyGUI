package com.spinyowl.spinygui.core.parser.impl.css.parser.visitor;

import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3BaseVisitor;
import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3Parser.KnownDeclarationContext;
import com.spinyowl.spinygui.core.style.stylesheet.Declaration;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyStore;
import com.spinyowl.spinygui.core.style.stylesheet.Term;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class PropertyVisitor extends CSS3BaseVisitor<Declaration> {

  @NonNull private final PropertyStore propertyStore;
  @NonNull private final PropertyValueVisitor propertyValueVisitor;

  @Override
  public Declaration visitKnownDeclaration(KnownDeclarationContext ctx) {
    String propertyName = ctx.property().getText();
    Property property = propertyStore.getProperty(propertyName);

    log.debug("Reading property: {}", propertyName);
    String stringValue = ctx.expr().getText();
    Term<?> term = propertyValueVisitor.visit(ctx.expr());


    if (property != null && stringValue != null) {
      log.debug("Reading property: {} - Done.\n", propertyName);
      return new Declaration(property, stringValue, term);
    }

    log.warn("Can't parse '{}' property with '{}' value", propertyName, stringValue);
    return super.visitKnownDeclaration(ctx);
  }
}
