package com.spinyowl.spinygui.core.parser.impl.css.visitor;

import com.spinyowl.spinygui.core.parser.impl.css.antlr.CSS3BaseVisitor;
import com.spinyowl.spinygui.core.parser.impl.css.antlr.CSS3Parser.KnownRulesetContext;
import com.spinyowl.spinygui.core.style.stylesheet.Declaration;
import com.spinyowl.spinygui.core.style.stylesheet.Ruleset;
import com.spinyowl.spinygui.core.style.stylesheet.selector.Selector;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RulesetVisitor extends CSS3BaseVisitor<Ruleset> {

  @NonNull private final CSS3BaseVisitor<List<Selector>> selectorGroupVisitor;
  @NonNull private final CSS3BaseVisitor<List<Declaration>> declarationListVisitor;

  /**
   * Grammar rule: selectorGroup '{' ws declarationList? '}' ws # knownRuleset
   *
   * @param ctx ruleset context.
   * @return read ruleset.
   */
  @Override
  public Ruleset visitKnownRuleset(KnownRulesetContext ctx) {

    var selectors = selectorGroupVisitor.visit(ctx.selectorGroup());
    var properties = declarationListVisitor.visit(ctx.declarationList());

    return new Ruleset(selectors, properties);
  }
}
