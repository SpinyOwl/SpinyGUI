package com.spinyowl.spinygui.core.converter.css.parser.visitor;

import com.spinyowl.spinygui.core.converter.css.parser.CSS3BaseVisitor;
import com.spinyowl.spinygui.core.converter.css.parser.CSS3Parser;
import com.spinyowl.spinygui.core.style.css.RuleSet;
import com.spinyowl.spinygui.core.style.css.StyleSheet;

import java.util.ArrayList;

public class StyleSheetVisitor extends CSS3BaseVisitor<StyleSheet> {


    @Override
    public StyleSheet visitStylesheet(CSS3Parser.StylesheetContext ctx) {
        var rulesets = new ArrayList<RuleSet>();
        for (CSS3Parser.NestedStatementContext nestedStatementCtx : ctx.nestedStatement()) {
            var rulset = new RulesetVisitor().visit(nestedStatementCtx);
            rulesets.add(rulset);
        }

        return new StyleSheet(rulesets);
    }
}
