package com.spinyowl.spinygui.core.parser.impl;

import com.spinyowl.spinygui.core.parser.StyleSheetParser;
import com.spinyowl.spinygui.core.parser.impl.css.parser.visitor.AtRuleVisitor;
import com.spinyowl.spinygui.core.parser.impl.css.parser.visitor.PropertyVisitor;
import com.spinyowl.spinygui.core.parser.impl.css.parser.visitor.RulesetVisitor;
import com.spinyowl.spinygui.core.parser.impl.css.parser.visitor.SelectorVisitor;
import com.spinyowl.spinygui.core.parser.impl.css.parser.visitor.StyleSheetVisitor;
import com.spinyowl.spinygui.core.style.stylesheet.PropertiesScanner;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyStore;
import com.spinyowl.spinygui.core.style.stylesheet.impl.DefaultPropertyStore;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StyleSheetParserFactory {

  public static StyleSheetParser createParser() {
    var propertyStore = getPropertyStore();
    var selectorVisitor = new SelectorVisitor();
    var propertyVisitor = new PropertyVisitor(propertyStore);
    var rulesetVisitor = new RulesetVisitor(selectorVisitor, propertyVisitor);
    var atRuleVisitor = new AtRuleVisitor();
    var styleSheetVisitor = new StyleSheetVisitor(rulesetVisitor, atRuleVisitor);
    return new DefaultStyleSheetParser(styleSheetVisitor);
  }

  private static PropertyStore getPropertyStore() {
    var propertyStore = new DefaultPropertyStore();
    PropertiesScanner.fillPropertyStore(propertyStore);
    return propertyStore;
  }
}
