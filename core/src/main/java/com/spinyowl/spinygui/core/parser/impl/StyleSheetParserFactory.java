package com.spinyowl.spinygui.core.parser.impl;

import com.spinyowl.spinygui.core.parser.StyleSheetParser;
import com.spinyowl.spinygui.core.parser.impl.css.parser.visitor.AtRuleVisitor;
import com.spinyowl.spinygui.core.parser.impl.css.parser.visitor.PropertyListVisitor;
import com.spinyowl.spinygui.core.parser.impl.css.parser.visitor.PropertyValueVisitor;
import com.spinyowl.spinygui.core.parser.impl.css.parser.visitor.PropertyVisitor;
import com.spinyowl.spinygui.core.parser.impl.css.parser.visitor.RulesetVisitor;
import com.spinyowl.spinygui.core.parser.impl.css.parser.visitor.SelectorGroupVisitor;
import com.spinyowl.spinygui.core.parser.impl.css.parser.visitor.SelectorVisitor;
import com.spinyowl.spinygui.core.parser.impl.css.parser.visitor.StyleSheetVisitor;
import com.spinyowl.spinygui.core.style.stylesheet.PropertiesScanner;
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
    return createParser(propertyStore, false);
  }

  /**
   * Creates stylesheet parser using {@link DefaultStyleSheetParser} implementation and scans for
   * properties.
   *
   * @param propertyStore property store with properties definitions.
   * @param scan if true - uses PropertiesScanner to scan for Properties and fill propertyStore.
   * @return style sheet parser.
   */
  public static StyleSheetParser createParser(PropertyStore propertyStore, boolean scan) {
    if (scan) {
      PropertiesScanner.fillPropertyStore(propertyStore);
    }

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
