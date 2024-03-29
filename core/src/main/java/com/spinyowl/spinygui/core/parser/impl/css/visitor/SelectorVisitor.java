package com.spinyowl.spinygui.core.parser.impl.css.visitor;

import com.spinyowl.spinygui.core.parser.impl.css.antlr.CSS3BaseVisitor;
import com.spinyowl.spinygui.core.parser.impl.css.antlr.CSS3Parser.ClassNameContext;
import com.spinyowl.spinygui.core.parser.impl.css.antlr.CSS3Parser.PseudoContext;
import com.spinyowl.spinygui.core.parser.impl.css.antlr.CSS3Parser.SelectorContext;
import com.spinyowl.spinygui.core.parser.impl.css.antlr.CSS3Parser.SimpleSelectorSequenceContext;
import com.spinyowl.spinygui.core.parser.impl.css.antlr.CSS3Parser.TypeSelectorContext;
import com.spinyowl.spinygui.core.parser.impl.css.antlr.CSS3Parser.UniversalContext;
import com.spinyowl.spinygui.core.style.stylesheet.selector.Selector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.combinator.AdjacentSiblingSelector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.combinator.AndSelector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.combinator.ChildSelector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.combinator.DescendantSelector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.combinator.GeneralSiblingSelector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.pseudoclass.HoverSelector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.pseudoelement.AfterSelector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.pseudoelement.BeforeSelector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.pseudoelement.ScrollbarSelector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.simple.AllSelector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.simple.ClassAttributeSelector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.simple.ElementSelector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.simple.IdAttributeSelector;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

@Slf4j
public class SelectorVisitor extends CSS3BaseVisitor<Selector> {

  public static final String NUMBER_SIGN = "#";

  @Override
  public Selector visitSelector(SelectorContext ctx) {
    var selector = visitSimpleSelectorSequence(ctx.simpleSelectorSequence(0));
    if (selector == null) return null;
    for (var i = 1; i < ctx.simpleSelectorSequence().size(); i++) {
      final var secondSelector = visitSimpleSelectorSequence(ctx.simpleSelectorSequence(i));
      if (secondSelector != null) {
        if (ctx.combinator(i - 1).Space() != null) {
          selector = new DescendantSelector(selector, secondSelector);
        } else if (ctx.combinator(i - 1).Greater() != null) {
          selector = new ChildSelector(selector, secondSelector);
        } else if (ctx.combinator(i - 1).Plus() != null) {
          selector = new AdjacentSiblingSelector(selector, secondSelector);
        } else if (ctx.combinator(i - 1).Tilde() != null) {
          selector = new GeneralSiblingSelector(selector, secondSelector);
        }
      }
    }
    return selector;
  }

  @Override
  public Selector visitSimpleSelectorSequence(SimpleSelectorSequenceContext ctx) {
    Selector current = null;
    for (ParseTree child : ctx.children) {
      Selector selector;
      if (child instanceof TerminalNode && child.getText().startsWith(NUMBER_SIGN)) { // id selector
        selector = new IdAttributeSelector(child.getText().substring(1));
      } else {
        selector = visit(child);
      }

      if (current == null && selector != null) {
        current = selector;
      } else if (current != null && selector != null) {
        current = new AndSelector(current, selector);
      } else {
        current = null; // force to null as it is combination of any and null which is unknown.
      }
    }

    return current;
  }

  @Override
  public Selector visitTypeSelector(TypeSelectorContext ctx) {
    return new ElementSelector(ctx.getText());
  }

  @Override
  public Selector visitPseudo(PseudoContext ctx) {
    String selectorName = ctx.ident().getText();
    if ("hover".equals(selectorName)) {
      return new HoverSelector();
    }

    if ("before".equals(selectorName)) {
      return new BeforeSelector();
    }

    if ("after".equals(selectorName)) {
      return new AfterSelector();
    }

    if ("scrollbar".equals(selectorName)) {
      return new ScrollbarSelector();
    }

    return null;
  }

  @Override
  public Selector visitClassName(ClassNameContext ctx) {
    return new ClassAttributeSelector(ctx.ident().getText());
  }

  @Override
  public Selector visitUniversal(UniversalContext ctx) {
    return new AllSelector();
  }
}
