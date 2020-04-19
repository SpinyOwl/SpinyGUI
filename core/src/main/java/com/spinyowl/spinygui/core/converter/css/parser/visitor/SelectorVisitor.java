package com.spinyowl.spinygui.core.converter.css.parser.visitor;

import com.spinyowl.spinygui.core.converter.css.parser.StyleReflectionHandler;
import com.spinyowl.spinygui.core.converter.css.parser.antlr.CSS3BaseVisitor;
import com.spinyowl.spinygui.core.converter.css.parser.antlr.CSS3Parser;
import com.spinyowl.spinygui.core.converter.css.selector.ClassNameSelector;
import com.spinyowl.spinygui.core.converter.css.selector.StyleSelector;
import com.spinyowl.spinygui.core.converter.css.selector.TypeSelector;
import com.spinyowl.spinygui.core.converter.dom.TagNameMapping;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.tree.ParseTree;

@Slf4j
public class SelectorVisitor extends CSS3BaseVisitor<List<StyleSelector>> {

  @Override
  public List<StyleSelector> visitSelectorGroup(CSS3Parser.SelectorGroupContext ctx) {

    List<StyleSelector> selectorVisitors = new ArrayList<>();

    for (CSS3Parser.SelectorContext selectorContext : ctx.selector()) {

      var selectors = visit(selectorContext);
      if (selectors != null) {
        selectorVisitors.addAll(selectors);
      }
    }

    return selectorVisitors;
  }

  @Override
  public List<StyleSelector> visitSelector(CSS3Parser.SelectorContext ctx) {
    var list = new ArrayList<StyleSelector>();
    var firstSelector = visitSimpleSelectorSequence(ctx.simpleSelectorSequence(0)).get(0);
    for (int i = 1; i < ctx.simpleSelectorSequence().size(); i++) {
      final var secondSelector = visitSimpleSelectorSequence(ctx.simpleSelectorSequence(i))
        .get(0);

      if (ctx.combinator(i - 1).Space() != null) {
        firstSelector = StyleSelector.child(firstSelector, secondSelector);
      } else if (ctx.combinator(i - 1).Greater() != null) {
        firstSelector = StyleSelector.immediateChild(firstSelector, secondSelector);
      } else if (ctx.combinator(i - 1).Plus() != null) {
        firstSelector = StyleSelector.immediateNext(firstSelector, secondSelector);
      } else if (ctx.combinator(i - 1).Tilde() != null) {
        firstSelector = StyleSelector.generalSibling(firstSelector, secondSelector);
      }
    }
    list.add(firstSelector);
    return list;
  }

  @Override
  public List<StyleSelector> visitSimpleSelectorSequence(
    CSS3Parser.SimpleSelectorSequenceContext ctx) {
    var list = new ArrayList<StyleSelector>();

    StyleSelector current = null;
    for (ParseTree child : ctx.children) {
      var s = visit(child).get(0);
      if (current == null) {
        current = s;
      } else {
        current = StyleSelector.and(current, s);
      }
    }
    list.add(current);

    return list;
  }

  @Override
  public List<StyleSelector> visitTypeSelector(CSS3Parser.TypeSelectorContext ctx) {
    var list = new ArrayList<StyleSelector>();
    var clazz = TagNameMapping.getElement(ctx.getText());
    list.add(new TypeSelector(clazz));
    return list;
  }

  @Override
  public List<StyleSelector> visitPseudo(CSS3Parser.PseudoContext ctx) {
    var list = new ArrayList<StyleSelector>();
    var clazz = StyleReflectionHandler.getPseudoSelector(ctx.getText());
    try {
      list.add((StyleSelector) clazz.getConstructor().newInstance());
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    }
    return list;
  }

  @Override
  public List<StyleSelector> visitClassName(CSS3Parser.ClassNameContext ctx) {
    var list = new ArrayList<StyleSelector>();

    var clazz = ctx.ident().getText();

    list.add(new ClassNameSelector(clazz));

    return list;
  }
}
