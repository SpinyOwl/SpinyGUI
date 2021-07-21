package com.spinyowl.spinygui.core.parser;

import com.spinyowl.spinygui.core.style.stylesheet.Declaration;
import com.spinyowl.spinygui.core.style.stylesheet.RuleSet;
import com.spinyowl.spinygui.core.style.stylesheet.StyleSheet;
import java.util.List;

/** Used to read stylesheets from css. */
public interface StyleSheetParser {

  StyleSheet parseStyleSheet(String css);

  List<Declaration> parseDeclarations(String css);

  String toCss(StyleSheet styleSheet);

  String toCss(RuleSet ruleSet);

  String toCss(Declaration declaration);
}
