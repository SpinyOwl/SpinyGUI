package com.spinyowl.spinygui.core.parser.impl.css.parser.visitor;

import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3BaseVisitor;
import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3Parser.DeclarationContext;
import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3Parser.KnownRulesetContext;
import com.spinyowl.spinygui.core.style.stylesheet.Declaration;
import com.spinyowl.spinygui.core.style.stylesheet.RuleSet;
import java.util.ArrayList;

public class RulesetVisitor extends CSS3BaseVisitor<RuleSet> {

  /**
   * grammar rule: selectorGroup '{' ws declarationList? '}' ws    # knownRuleset
   *
   * @param ctx
   * @return
   */
  @Override
  public RuleSet visitKnownRuleset(KnownRulesetContext ctx) {

    var selectors = new SelectorVisitor().visit(ctx.selectorGroup());
    var properties = new ArrayList<Declaration<?>>();
    for (DeclarationContext declarationCtx : ctx.declarationList().declaration()) {
      var rule = new PropertyVisitor().visit(declarationCtx);
      if (rule != null) {
        properties.add(rule);
      }
    }

    return new RuleSet(selectors, properties);
  }
}
