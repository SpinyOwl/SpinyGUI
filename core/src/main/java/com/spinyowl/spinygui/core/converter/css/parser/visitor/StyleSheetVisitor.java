package com.spinyowl.spinygui.core.converter.css.parser.visitor;

import com.spinyowl.spinygui.core.converter.css.StyleSheet;
import com.spinyowl.spinygui.core.converter.css.parser.antlr.CSS3BaseVisitor;
import com.spinyowl.spinygui.core.converter.css.parser.antlr.CSS3Parser;
import java.util.Objects;
import java.util.stream.Collectors;

public class StyleSheetVisitor extends CSS3BaseVisitor<StyleSheet> {

  @Override
  public StyleSheet visitStylesheet(CSS3Parser.StylesheetContext ctx) {
    var rulesetVisitor = new RulesetVisitor();

    var ruleSetList = ctx.nestedStatement()
      .stream().map(rulesetVisitor::visit)
      .filter(Objects::nonNull).collect(Collectors.toList());

    var rules = ctx.nestedStatement()
      .stream().map(new AtRuleVisitor()::visit)
      .filter(Objects::nonNull).collect(Collectors.toList());

    return new StyleSheet(ruleSetList, rules);
  }
}
