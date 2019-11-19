package com.spinyowl.spinygui.core.converter;

import com.spinyowl.spinygui.core.converter.css.parser.CSS3Lexer;
import com.spinyowl.spinygui.core.converter.css.parser.CSS3Parser;
import com.spinyowl.spinygui.core.converter.css.parser.StyleSheetException;
import com.spinyowl.spinygui.core.converter.css.parser.visitor.StyleSheetVisitor;
import com.spinyowl.spinygui.core.style.css.StyleSheet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public final class StyleSheetConverter {
    private static final Log LOGGER = LogFactory.getLog(StyleSheetConverter.class);

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
            LOGGER.error(e.getMessage());
            throw new StyleSheetException(e);
        }
    }

//    /**
//     * Used to create StyleSheet from css.
//     *
//     * @param css css source
//     * @return StyleSheet
//     */
//    public static List<> createFromCSS(String css) throws StyleSheetException {
//
//        try {
//            var charStream = CharStreams.fromString(css);
//            var lexer = new CSS3Lexer(charStream);
//
//            var tokenStream = new CommonTokenStream(lexer);
//            var parser = new CSS3Parser(tokenStream);
//
//            CSS3Parser.StylesheetContext stylesheet = parser.stylesheet();
//            return new StyleSheetVisitor().visit(stylesheet);
//
//        } catch (Exception e) {
//            LOGGER.error(e.getMessage());
//            throw new StyleSheetException(e);
//        }
//    }
}
