package com.spinyowl.spinygui.core.converter;

import com.spinyowl.spinygui.core.converter.css.model.StyleSheet;
import com.spinyowl.spinygui.core.converter.css.parser.StyleSheetException;
import com.spinyowl.spinygui.core.converter.css.parser.antlr.CSS3Lexer;
import com.spinyowl.spinygui.core.converter.css.parser.antlr.CSS3Parser;
import com.spinyowl.spinygui.core.converter.css.parser.visitor.StyleSheetVisitor;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public final class StyleSheetConverter {

  private StyleSheetConverter() {
  }

  /**
   * Used to create StyleSheet from css.
   *
   * @param css css source
   * @return StyleSheet
   */
  public static StyleSheet createFromCSS(String css) throws StyleSheetException {

    try {
      var charStream = CharStreams.fromString(css);
      var lexer = new CSS3Lexer(charStream);

      var tokenStream = new CommonTokenStream(lexer);
      var parser = new CSS3Parser(tokenStream);

      CSS3Parser.StylesheetContext stylesheet = parser.stylesheet();
      return new StyleSheetVisitor().visit(stylesheet);

    } catch (Exception e) {
      throw new StyleSheetException(e);
    }
  }

}
