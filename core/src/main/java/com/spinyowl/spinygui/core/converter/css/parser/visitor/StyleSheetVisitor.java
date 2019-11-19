package com.spinyowl.spinygui.core.converter.css.parser.visitor;

import com.spinyowl.spinygui.core.converter.css.parser.CSS3BaseVisitor;
import com.spinyowl.spinygui.core.converter.css.parser.CSS3Parser;
import com.spinyowl.spinygui.core.style.css.StyleSheet;

import java.util.stream.Collectors;

public class StyleSheetVisitor extends CSS3BaseVisitor<StyleSheet> {


    @Override
    public StyleSheet visitStylesheet(CSS3Parser.StylesheetContext ctx) {
        var ruleSetList = ctx.nestedStatement()
                .stream().map(nCtx -> new RulesetVisitor().visit(nCtx))
                .collect(Collectors.toList());

        return new StyleSheet(ruleSetList);
    }
}
