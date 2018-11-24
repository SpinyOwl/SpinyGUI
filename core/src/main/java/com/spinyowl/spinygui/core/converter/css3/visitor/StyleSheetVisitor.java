package com.spinyowl.spinygui.core.converter.css3.visitor;

import com.spinyowl.spinygui.core.converter.css3.CSS3BaseVisitor;
import com.spinyowl.spinygui.core.converter.css3.CSS3Parser;
import com.spinyowl.spinygui.core.style.StyleSheet;

public class StyleSheetVisitor extends CSS3BaseVisitor<StyleSheet> {


    @Override
    public StyleSheet visitStylesheet(CSS3Parser.StylesheetContext ctx) {


        for (CSS3Parser.NestedStatementContext nestedStatementCtx : ctx.nestedStatement()) {
            var rulset = new RulesetVisitor().visit(nestedStatementCtx);
        }


        return super.visitStylesheet(ctx);
    }
}
