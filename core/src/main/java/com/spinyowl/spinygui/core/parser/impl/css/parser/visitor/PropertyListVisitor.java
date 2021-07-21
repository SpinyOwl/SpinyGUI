package com.spinyowl.spinygui.core.parser.impl.css.parser.visitor;

import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3BaseVisitor;
import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3Parser.DeclarationListContext;
import com.spinyowl.spinygui.core.style.stylesheet.Declaration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class PropertyListVisitor extends CSS3BaseVisitor<List<Declaration>> {

  @NonNull private final PropertyVisitor propertyVisitor;

  @Override
  public List<Declaration> visitDeclarationList(DeclarationListContext ctx) {
    return ctx.declaration().stream()
        .map(propertyVisitor::visit)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }
}
