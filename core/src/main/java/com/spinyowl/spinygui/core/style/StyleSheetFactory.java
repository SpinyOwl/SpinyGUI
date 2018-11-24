package com.spinyowl.spinygui.core.style;

import com.spinyowl.spinygui.core.converter.css3.CSS3Lexer;
import com.spinyowl.spinygui.core.converter.css3.CSS3Parser;
import com.spinyowl.spinygui.core.converter.css3.StyleSheetException;
import com.spinyowl.spinygui.core.converter.css3.visitor.StyleSheetVisitor;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public class StyleSheetFactory {

    /**
     * @param css
     * @return
     */
    static StyleSheet createFromCSS(String css) throws StyleSheetException {

        try {
            var charStream = CharStreams.fromString(css);
            var lexer = new CSS3Lexer(charStream);

            var tokenStream = new CommonTokenStream(lexer);
            var parser = new CSS3Parser(tokenStream);

            var styleSheet = new StyleSheetVisitor().visit(parser.stylesheet());
            return styleSheet;

        } catch (Exception e) {
            //TODO: Proper exception handling
            e.printStackTrace();
            throw new StyleSheetException();
        }
    }
}
