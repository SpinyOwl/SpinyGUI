package com.spinyowl.spinygui.core.parser.impl.css.visitor;

import com.spinyowl.spinygui.core.parser.impl.css.antlr.CSS3BaseVisitor;
import com.spinyowl.spinygui.core.parser.impl.css.antlr.CSS3Parser.DeclarationListContext;
import com.spinyowl.spinygui.core.style.stylesheet.Declaration;
import java.util.List;
import java.util.Objects;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class DeclarationListVisitor extends CSS3BaseVisitor<List<Declaration>> {

  @NonNull private final DeclarationVisitor declarationVisitor;

  @Override
  public List<Declaration> visitDeclarationList(DeclarationListContext ctx) {
    return ctx.declaration().stream()
        .map(declarationVisitor::visit)
        .filter(Objects::nonNull)
        .toList();
  }
}
