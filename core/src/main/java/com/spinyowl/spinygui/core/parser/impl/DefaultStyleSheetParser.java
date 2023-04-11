package com.spinyowl.spinygui.core.parser.impl;

import com.spinyowl.spinygui.core.parser.StyleSheetParser;
import com.spinyowl.spinygui.core.parser.impl.css.antlr.CSS3BaseVisitor;
import com.spinyowl.spinygui.core.parser.impl.css.antlr.CSS3Lexer;
import com.spinyowl.spinygui.core.parser.impl.css.antlr.CSS3Parser;
import com.spinyowl.spinygui.core.style.stylesheet.Declaration;
import com.spinyowl.spinygui.core.style.stylesheet.Ruleset;
import com.spinyowl.spinygui.core.style.stylesheet.StyleSheet;
import com.spinyowl.spinygui.core.style.stylesheet.selector.Selector;
import java.util.List;
import java.util.StringJoiner;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

/** Used to read stylesheets from css. */
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
  public StyleSheet parse(@NonNull String css) {
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
    for (Ruleset ruleSet : styleSheet.rulesets()) {

      builder.append(toCss(ruleSet)).append("\n");
    }
    return builder.toString();
  }

  public String toCss(Ruleset ruleSet) {
    StringBuilder builder = new StringBuilder();
    var selectorJoiner = new StringJoiner(", ", "", " ");
    for (Selector selector : ruleSet.selectors()) {
      selectorJoiner.add(selector.toString());
    }
    builder.append(selectorJoiner).append("{");

    var declarationsJoiner = new StringJoiner(";\n  ", "\n  ", ";\n");
    for (Declaration declaration : ruleSet.declarations()) {
      declarationsJoiner.add("  " + toCss(declaration));
    }
    String declarations = declarationsJoiner.toString();
    builder.append(declarations).append("}");
    return builder.toString();
  }

  public String toCss(Declaration declaration) {
    return declaration.property().name() + ": " + declaration.term().toString();
  }
}
