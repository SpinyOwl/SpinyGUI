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
import com.spinyowl.spinygui.core.style.stylesheet.selector.simple.AllSelector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.simple.ClassAttributeSelector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.simple.ElementSelector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.simple.IdAttributeSelector;
import java.util.List;
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

  @NonNull private final CSS3BaseVisitor<StyleSheet> styleSheetVisitor;
  @NonNull private final CSS3BaseVisitor<List<Declaration>> declarationListVisitor;

  /**
   * Used to create StyleSheet from css.
   *
   * @param css css source
   * @return StyleSheet
   */
  public StyleSheet parseStyleSheet(@NonNull String css) {
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

  @Override
  public List<Declaration> parseDeclarations(String css) {
    try {
      var charStream = CharStreams.fromString(css);
      var lexer = new CSS3Lexer(charStream);

      var tokenStream = new CommonTokenStream(lexer);
      var parser = new CSS3Parser(tokenStream);
      return declarationListVisitor.visit(parser.declarationList());
    } catch (Exception e) {
      throw new ParseException(e);
    }
  }

  public String toCss(@NonNull StyleSheet styleSheet) {
    var builder = new StringBuilder();
    for (RuleSet ruleSet : styleSheet.ruleSets()) {

      builder.append(toCss(ruleSet)).append("\n");
    }
    return builder.toString();
  }

  public String toCss(RuleSet ruleSet) {
    StringBuilder builder = new StringBuilder();
    var selectorJoiner = new StringJoiner(", ", "", " ");
    for (Selector selector : ruleSet.selectors()) {
      selectorJoiner.add(getSelectorString(selector));
    }
    builder.append(selectorJoiner).append("{");

    var declarationsJoiner = new StringJoiner(";\n", "\n", ";\n");
    for (Declaration declaration : ruleSet.declarations()) {
      declarationsJoiner.add("  " + toCss(declaration));
    }
    String declarations = declarationsJoiner.toString();
    builder.append(declarations).append("}");
    return builder.toString();
  }

  public String toCss(Declaration declaration) {
    return declaration.property().name() + ": " + declaration.value();
  }

  String getSelectorString(Selector selector) {
    if (selector instanceof AdjacentSiblingSelector adjacentSelector) {
      return getSelectorString(adjacentSelector.first()) + " + "
          + getSelectorString(adjacentSelector.second());
    } else if (selector instanceof AndSelector andSelector) {
      return getSelectorString(andSelector.first())
          + getSelectorString(andSelector.second());
    } else if (selector instanceof ChildSelector childSelector) {
      return getSelectorString(childSelector.first()) + " > "
          + getSelectorString(childSelector.second());
    } else if (selector instanceof DescendantSelector descendantSelector) {
      return getSelectorString(descendantSelector.first()) + " "
          + getSelectorString(descendantSelector.second());
    } else if (selector instanceof GeneralSiblingSelector siblingSelector) {
      return getSelectorString(siblingSelector.first()) + " ~ "
          + getSelectorString(siblingSelector.second());
    } else if (selector instanceof HoverSelector) {
      return ":hover";
    } else if (selector instanceof ClassAttributeSelector classSelector) {
      return "." + classSelector.className();
    } else if (selector instanceof ElementSelector elementSelector) {
      return elementSelector.nodeName();
    } else if (selector instanceof AllSelector) {
      return "*";
    } else if (selector instanceof IdAttributeSelector idSelector) {
      return "#" + idSelector.id();
    }
    return "";
  }
}
