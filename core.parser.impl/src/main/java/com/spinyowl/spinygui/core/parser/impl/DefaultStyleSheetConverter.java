package com.spinyowl.spinygui.core.parser.impl;

import com.spinyowl.spinygui.core.parser.StyleSheetConverter;
import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3Lexer;
import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3Parser;
import com.spinyowl.spinygui.core.parser.impl.css.parser.visitor.StyleSheetVisitor;
import com.spinyowl.spinygui.core.style.stylesheet.StyleSheet;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public final class DefaultStyleSheetConverter implements StyleSheetConverter {

  /**
   * Used to create StyleSheet from css.
   *
   * @param css css source
   * @return StyleSheet
   */
  public StyleSheet fromCss(String css) {

    try {
      var charStream = CharStreams.fromString(css);
      var lexer = new CSS3Lexer(charStream);

      var tokenStream = new CommonTokenStream(lexer);
      var parser = new CSS3Parser(tokenStream);

      CSS3Parser.StylesheetContext stylesheet = parser.stylesheet();
      return new StyleSheetVisitor().visit(stylesheet);

    } catch (Exception e) {
      throw new ParseException(e);
    }
  }
}
