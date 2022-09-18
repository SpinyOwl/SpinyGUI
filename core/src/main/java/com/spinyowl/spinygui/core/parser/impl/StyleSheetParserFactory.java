package com.spinyowl.spinygui.core.parser.impl;

import com.spinyowl.spinygui.core.parser.StyleSheetParser;
import com.spinyowl.spinygui.core.parser.impl.css.visitor.AtRuleVisitor;
import com.spinyowl.spinygui.core.parser.impl.css.visitor.PropertyListVisitor;
import com.spinyowl.spinygui.core.parser.impl.css.visitor.PropertyValueVisitor;
import com.spinyowl.spinygui.core.parser.impl.css.visitor.PropertyVisitor;
import com.spinyowl.spinygui.core.parser.impl.css.visitor.RulesetVisitor;
import com.spinyowl.spinygui.core.parser.impl.css.visitor.SelectorGroupVisitor;
import com.spinyowl.spinygui.core.parser.impl.css.visitor.SelectorVisitor;
import com.spinyowl.spinygui.core.parser.impl.css.visitor.StyleSheetVisitor;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyStore;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StyleSheetParserFactory {

  /**
   * Creates stylesheet parser using {@link DefaultStyleSheetParser} implementation.
   *
   * @param propertyStore property store with properties definitions.
   * @return style sheet parser.
   */
  public static StyleSheetParser createParser(PropertyStore propertyStore) {
    var selectorVisitor = new SelectorVisitor();
    var selectorGroupVisitor = new SelectorGroupVisitor(selectorVisitor);
    var propertyValueVisitor = new PropertyValueVisitor();
    var propertyVisitor = new PropertyVisitor(propertyStore, propertyValueVisitor);
    var propertyListVisitor = new PropertyListVisitor(propertyVisitor);
    var rulesetVisitor = new RulesetVisitor(selectorGroupVisitor, propertyListVisitor);
    var atRuleVisitor = new AtRuleVisitor();
    var styleSheetVisitor = new StyleSheetVisitor(rulesetVisitor, atRuleVisitor);
    return new DefaultStyleSheetParser(styleSheetVisitor, propertyListVisitor);
  }
}
