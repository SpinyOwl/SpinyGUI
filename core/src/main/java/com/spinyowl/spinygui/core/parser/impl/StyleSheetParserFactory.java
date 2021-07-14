package com.spinyowl.spinygui.core.parser.impl;

import com.spinyowl.spinygui.core.parser.StyleSheetParser;
import com.spinyowl.spinygui.core.parser.impl.css.parser.visitor.AtRuleVisitor;
import com.spinyowl.spinygui.core.parser.impl.css.parser.visitor.PropertyVisitor;
import com.spinyowl.spinygui.core.parser.impl.css.parser.visitor.RulesetVisitor;
import com.spinyowl.spinygui.core.parser.impl.css.parser.visitor.SelectorVisitor;
import com.spinyowl.spinygui.core.parser.impl.css.parser.visitor.StyleSheetVisitor;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyStore;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StyleSheetParserFactory {

  public static StyleSheetParser createParser(PropertyStore propertyStore) {
    var selectorVisitor = new SelectorVisitor();
    var propertyVisitor = new PropertyVisitor(propertyStore);
    var rulesetVisitor = new RulesetVisitor(selectorVisitor, propertyVisitor);
    var atRuleVisitor = new AtRuleVisitor();
    var styleSheetVisitor = new StyleSheetVisitor(rulesetVisitor, atRuleVisitor);
    return new DefaultStyleSheetParser(styleSheetVisitor);
  }
}
