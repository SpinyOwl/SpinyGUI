package com.spinyowl.spinygui.core.parser.impl.css.parser.visitor;

import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3BaseVisitor;
import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3Parser.DeclarationContext;
import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3Parser.KnownRulesetContext;
import com.spinyowl.spinygui.core.style.stylesheet.Declaration;
import com.spinyowl.spinygui.core.style.stylesheet.RuleSet;
import com.spinyowl.spinygui.core.style.stylesheet.selector.Selector;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RulesetVisitor extends CSS3BaseVisitor<RuleSet> {

  @NonNull private final CSS3BaseVisitor<List<Selector>> selectorVisitor;
  @NonNull private final CSS3BaseVisitor<Declaration<?>> propertyVisitor;

  /**
   * Grammar rule: selectorGroup '{' ws declarationList? '}' ws # knownRuleset
   *
   * @param ctx ruleset context.
   * @return read ruleset.
   */
  @Override
  public RuleSet visitKnownRuleset(KnownRulesetContext ctx) {

    var selectors = selectorVisitor.visit(ctx.selectorGroup());
    var properties = new ArrayList<Declaration<?>>();
    for (DeclarationContext declarationCtx : ctx.declarationList().declaration()) {
      var rule = propertyVisitor.visit(declarationCtx);
      if (rule != null) {
        properties.add(rule);
      }
    }

    return new RuleSet(selectors, properties);
  }
}
