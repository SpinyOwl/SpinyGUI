package com.spinyowl.spinygui.core.parser.impl.css.visitor;

import com.spinyowl.spinygui.core.parser.impl.css.antlr.CSS3BaseVisitor;
import com.spinyowl.spinygui.core.parser.impl.css.antlr.CSS3Parser.StylesheetContext;
import com.spinyowl.spinygui.core.style.stylesheet.AtRule;
import com.spinyowl.spinygui.core.style.stylesheet.Ruleset;
import com.spinyowl.spinygui.core.style.stylesheet.StyleSheet;
import java.util.Objects;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StyleSheetVisitor extends CSS3BaseVisitor<StyleSheet> {

  @NonNull private final CSS3BaseVisitor<Ruleset> rulesetVisitor;
  @NonNull private final CSS3BaseVisitor<AtRule> atRuleVisitor;

  @Override
  public StyleSheet visitStylesheet(StylesheetContext ctx) {
    var ruleSetList =
        ctx.nestedStatement().stream().map(rulesetVisitor::visit).filter(Objects::nonNull).toList();

    var rules =
        ctx.nestedStatement().stream().map(atRuleVisitor::visit).filter(Objects::nonNull).toList();

    return new StyleSheet(ruleSetList, rules);
  }
}
