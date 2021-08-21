package com.spinyowl.spinygui.core.parser.impl.css.parser.visitor;

import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3BaseVisitor;
import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3Parser.SelectorGroupContext;
import com.spinyowl.spinygui.core.style.stylesheet.selector.Selector;
import java.util.List;
import java.util.Objects;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class SelectorGroupVisitor extends CSS3BaseVisitor<List<Selector>> {

  public static final String NUMBER_SIGN = "#";
  @NonNull private final CSS3BaseVisitor<Selector> selectorVisitor;

  @Override
  public List<Selector> visitSelectorGroup(SelectorGroupContext ctx) {
    return ctx.selector().stream().map(selectorVisitor::visit).filter(Objects::nonNull).toList();
  }
}
