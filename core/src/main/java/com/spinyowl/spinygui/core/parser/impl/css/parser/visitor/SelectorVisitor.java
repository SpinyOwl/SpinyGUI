package com.spinyowl.spinygui.core.parser.impl.css.parser.visitor;

import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3BaseVisitor;
import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3Parser.ClassNameContext;
import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3Parser.PseudoContext;
import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3Parser.SelectorContext;
import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3Parser.SelectorGroupContext;
import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3Parser.SimpleSelectorSequenceContext;
import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3Parser.TypeSelectorContext;
import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3Parser.UniversalContext;
import com.spinyowl.spinygui.core.style.stylesheet.selector.Selector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.combinator.AdjacentSiblingSelector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.combinator.AndSelector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.combinator.ChildSelector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.combinator.DescendantSelector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.combinator.GeneralSiblingSelector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.pseudo_class.HoverSelector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.simple.AllSelector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.simple.ClassAttributeSelector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.simple.ElementSelector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.simple.IdAttributeSelector;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

@Slf4j
public class SelectorVisitor extends CSS3BaseVisitor<List<Selector>> {

  public static final String NUMBER_SIGN = "#";

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
        firstSelector = new DescendantSelector(firstSelector, secondSelector);
      } else if (ctx.combinator(i - 1).Greater() != null) {
        firstSelector = new ChildSelector(firstSelector, secondSelector);
      } else if (ctx.combinator(i - 1).Plus() != null) {
        firstSelector = new AdjacentSiblingSelector(firstSelector, secondSelector);
      } else if (ctx.combinator(i - 1).Tilde() != null) {
        firstSelector = new GeneralSiblingSelector(firstSelector, secondSelector);
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
      var s = getSelector(child);
      if (current == null) {
        current = s;
      } else {
        current = new AndSelector(current, s);
      }
    }
    list.add(current);

    return list;
  }

  private Selector getSelector(ParseTree child) {
    Selector selector;
    if (child instanceof TerminalNode && child.getText().startsWith(NUMBER_SIGN)) { // id selector
      selector = new IdAttributeSelector(child.getText().substring(1));
    } else {
      selector = visit(child).get(0);
    }
    return selector;
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

  @Override
  public List<Selector> visitUniversal(UniversalContext ctx) {
    return List.of(new AllSelector());
  }
}
