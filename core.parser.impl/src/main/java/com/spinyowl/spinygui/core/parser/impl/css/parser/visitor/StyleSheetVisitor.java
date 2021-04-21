package com.spinyowl.spinygui.core.parser.impl.css.parser.visitor;

import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3BaseVisitor;
import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3Parser.StylesheetContext;
import com.spinyowl.spinygui.core.style.stylesheet.StyleSheet;
import java.util.Objects;
import java.util.stream.Collectors;

public class StyleSheetVisitor extends CSS3BaseVisitor<StyleSheet> {

  @Override
  public StyleSheet visitStylesheet(StylesheetContext ctx) {
    var rulesetVisitor = new RulesetVisitor();

    var ruleSetList =
        ctx.nestedStatement().stream()
            .map(rulesetVisitor::visit)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

    var rules =
        ctx.nestedStatement().stream()
            .map(new AtRuleVisitor()::visit)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

    return new StyleSheet(ruleSetList, rules);
  }
}
