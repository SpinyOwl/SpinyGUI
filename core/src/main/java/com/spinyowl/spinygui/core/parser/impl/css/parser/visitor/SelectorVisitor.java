package com.spinyowl.spinygui.core.parser.impl.css.parser.visitor;

import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3BaseVisitor;
import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3Parser.ClassNameContext;
import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3Parser.PseudoContext;
import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3Parser.SelectorContext;
import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3Parser.SelectorGroupContext;
import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3Parser.SimpleSelectorSequenceContext;
import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3Parser.TypeSelectorContext;
import com.spinyowl.spinygui.core.style.stylesheet.selector.Selector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.Selectors;
import com.spinyowl.spinygui.core.style.stylesheet.selector.pseudo_class.HoverSelector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.simple.ClassAttributeSelector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.simple.ElementSelector;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.tree.ParseTree;

@Slf4j
public class SelectorVisitor extends CSS3BaseVisitor<List<Selector>> {

  @Override
  public List<Selector> visitSelectorGroup(SelectorGroupContext ctx) {

    List<Selector> selectorVisitors = new ArrayList<>();

    for (SelectorContext selectorContext : ctx.selector()) {

      var selectors = visit(selectorContext);
      if (selectors != null) {
        selectorVisitors.addAll(selectors);
      }
    }

    return selectorVisitors;
  }

  @Override
  public List<Selector> visitSelector(SelectorContext ctx) {
    var list = new ArrayList<Selector>();
    var firstSelector = visitSimpleSelectorSequence(ctx.simpleSelectorSequence(0)).get(0);
    for (var i = 1; i < ctx.simpleSelectorSequence().size(); i++) {
      final var secondSelector = visitSimpleSelectorSequence(ctx.simpleSelectorSequence(i)).get(0);

      if (ctx.combinator(i - 1).Space() != null) {
        firstSelector = Selectors.descendant(firstSelector, secondSelector);
      } else if (ctx.combinator(i - 1).Greater() != null) {
        firstSelector = Selectors.child(firstSelector, secondSelector);
      } else if (ctx.combinator(i - 1).Plus() != null) {
        firstSelector = Selectors.adjacentSibling(firstSelector, secondSelector);
      } else if (ctx.combinator(i - 1).Tilde() != null) {
        firstSelector = Selectors.generalSibling(firstSelector, secondSelector);
      }
    }
    list.add(firstSelector);
    return list;
  }

  @Override
  public List<Selector> visitSimpleSelectorSequence(SimpleSelectorSequenceContext ctx) {
    var list = new ArrayList<Selector>();

    Selector current = null;
    for (ParseTree child : ctx.children) {
      var s = visit(child).get(0);
      if (current == null) {
        current = s;
      } else {
        current = Selectors.and(current, s);
      }
    }
    list.add(current);

    return list;
  }

  @Override
  public List<Selector> visitTypeSelector(TypeSelectorContext ctx) {
    return List.of(new ElementSelector(ctx.getText()));
  }

  @Override
  public List<Selector> visitPseudo(PseudoContext ctx) {
    String selectorName = ctx.ident().getText();

    if ("hover".equals(selectorName)) {
      return List.of(new HoverSelector());
    }
    return List.of();
  }

  @Override
  public List<Selector> visitClassName(ClassNameContext ctx) {
    return List.of(new ClassAttributeSelector(ctx.ident().getText()));
  }
}
