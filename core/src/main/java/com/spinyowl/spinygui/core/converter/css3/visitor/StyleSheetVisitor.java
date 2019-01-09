package com.spinyowl.spinygui.core.converter.css3.visitor;

import com.spinyowl.spinygui.core.converter.css3.CSS3BaseVisitor;
import com.spinyowl.spinygui.core.converter.css3.CSS3Parser;
import com.spinyowl.spinygui.core.style.RuleSet;
import com.spinyowl.spinygui.core.style.StyleSheet;

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
