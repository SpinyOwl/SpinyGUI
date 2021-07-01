package com.spinyowl.spinygui.core.parser.impl;

import com.spinyowl.spinygui.core.parser.StyleSheetParser;
import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3BaseVisitor;
import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3Lexer;
import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3Parser;
import com.spinyowl.spinygui.core.style.stylesheet.Declaration;
import com.spinyowl.spinygui.core.style.stylesheet.RuleSet;
import com.spinyowl.spinygui.core.style.stylesheet.StyleSheet;
import com.spinyowl.spinygui.core.style.stylesheet.selector.Selector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.combinator.AdjacentSiblingSelector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.combinator.AndSelector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.combinator.ChildSelector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.combinator.DescendantSelector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.combinator.GeneralSiblingSelector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.pseudo_class.HoverSelector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.simple.ClassAttributeSelector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.simple.ElementSelector;
import java.util.StringJoiner;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

/**
 * Used to read stylesheets from css.
 */
@RequiredArgsConstructor
public final class DefaultStyleSheetParser implements StyleSheetParser {

  @NonNull
  private final CSS3BaseVisitor<StyleSheet> styleSheetVisitor;

  /**
   * Used to create StyleSheet from css.
   *
   * @param css css source
   * @return StyleSheet
   */
  public StyleSheet fromCss(@NonNull String css) {
    try {
      var charStream = CharStreams.fromString(css);
      var lexer = new CSS3Lexer(charStream);

      var tokenStream = new CommonTokenStream(lexer);
      var parser = new CSS3Parser(tokenStream);

      CSS3Parser.StylesheetContext stylesheet = parser.stylesheet();
      return styleSheetVisitor.visit(stylesheet);

    } catch (Exception e) {
      throw new ParseException(e);
    }
  }

  public String toCss(@NonNull StyleSheet styleSheet) {
    StringBuilder builder = new StringBuilder();
    for (RuleSet ruleSet : styleSheet.ruleSets()) {
      var selectorJoiner = new StringJoiner(", ", "", " ");
      for (Selector selector : ruleSet.selectors()) {
        selectorJoiner.add(getSelectorString(selector));
      }
      builder.append(selectorJoiner).append("{");

      var declarationsJoiner = new StringJoiner(";\n", "\n", ";\n");
      for (Declaration<?> declaration : ruleSet.declarations()) {
        declarationsJoiner.add("  " + (declaration.property().name() + ": " + declaration.value()));
      }
      builder.append(declarationsJoiner).append("}\n");
    }
    return builder.toString();
  }

  private String getSelectorString(Selector selector) {
    var selectorString = "";
    if (selector instanceof AdjacentSiblingSelector adjacentSiblingSelector) {
      selectorString = getSelectorString(adjacentSiblingSelector.first()) + " + "
          + getSelectorString(adjacentSiblingSelector.second());
    } else if (selector instanceof AndSelector andSelector) {
      selectorString = getSelectorString(andSelector.first())
          + getSelectorString(andSelector.second());
    } else if (selector instanceof ChildSelector childSelector) {
      selectorString = getSelectorString(childSelector.first()) + " > "
          + getSelectorString(childSelector.second());
    } else if (selector instanceof DescendantSelector descendantSelector) {
      selectorString = getSelectorString(descendantSelector.first()) + " "
          + getSelectorString(descendantSelector.second());
    } else if (selector instanceof GeneralSiblingSelector generalSiblingSelector) {
      selectorString = getSelectorString(generalSiblingSelector.first()) + " ~ "
          + getSelectorString(generalSiblingSelector.second());
    } else if (selector instanceof HoverSelector) {
      selectorString = ":hover";
    } else if (selector instanceof ClassAttributeSelector classAttributeSelector) {
      selectorString = "." + classAttributeSelector.className();
    } else if (selector instanceof ElementSelector elementSelector) {
      selectorString = elementSelector.nodeName();
    }
    return selectorString;
  }
}
