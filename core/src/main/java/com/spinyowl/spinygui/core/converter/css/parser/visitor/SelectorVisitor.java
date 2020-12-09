package com.spinyowl.spinygui.core.converter.css.parser.visitor;

import com.spinyowl.spinygui.core.converter.css.model.selector.Selector;
import com.spinyowl.spinygui.core.converter.css.model.selector.Selectors;
import com.spinyowl.spinygui.core.converter.css.model.selector.pseudo_class.HoverSelector;
import com.spinyowl.spinygui.core.converter.css.model.selector.simple.ClassNameSelector;
import com.spinyowl.spinygui.core.converter.css.model.selector.simple.ElementSelector;
import com.spinyowl.spinygui.core.converter.css.parser.antlr.CSS3BaseVisitor;
import com.spinyowl.spinygui.core.converter.css.parser.antlr.CSS3Parser;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.tree.ParseTree;

@Slf4j
public class SelectorVisitor extends CSS3BaseVisitor<List<Selector>> {

  @Override
  public List<Selector> visitSelectorGroup(CSS3Parser.SelectorGroupContext ctx) {

    List<Selector> selectorVisitors = new ArrayList<>();

    for (CSS3Parser.SelectorContext selectorContext : ctx.selector()) {

      var selectors = visit(selectorContext);
      if (selectors != null) {
        selectorVisitors.addAll(selectors);
      }
    }

    return selectorVisitors;
  }

  @Override
  public List<Selector> visitSelector(CSS3Parser.SelectorContext ctx) {
    var list = new ArrayList<Selector>();
    var firstSelector = visitSimpleSelectorSequence(ctx.simpleSelectorSequence(0)).get(0);
    for (int i = 1; i < ctx.simpleSelectorSequence().size(); i++) {
      final var secondSelector = visitSimpleSelectorSequence(ctx.simpleSelectorSequence(i))
          .get(0);

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
  public List<Selector> visitSimpleSelectorSequence(
      CSS3Parser.SimpleSelectorSequenceContext ctx) {
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
  public List<Selector> visitTypeSelector(CSS3Parser.TypeSelectorContext ctx) {
    return List.of(new ElementSelector(ctx.getText()));
  }

  @Override
  public List<Selector> visitPseudo(CSS3Parser.PseudoContext ctx) {
    String selectorName = ctx.ident().getText();

    return switch (selectorName) {
      case "hover" -> List.of(new HoverSelector());
      default -> List.of();
    };
  }

  @Override
  public List<Selector> visitClassName(CSS3Parser.ClassNameContext ctx) {
    return List.of(new ClassNameSelector(ctx.ident().getText()));
  }
}
