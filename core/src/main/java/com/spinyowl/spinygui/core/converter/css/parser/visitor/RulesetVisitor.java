package com.spinyowl.spinygui.core.converter.css.parser.visitor;

import com.spinyowl.spinygui.core.converter.css.Declaration;
import com.spinyowl.spinygui.core.converter.css.RuleSet;
import com.spinyowl.spinygui.core.converter.css.parser.antlr.CSS3BaseVisitor;
import com.spinyowl.spinygui.core.converter.css.parser.antlr.CSS3Parser;
import java.util.ArrayList;

public class RulesetVisitor extends CSS3BaseVisitor<RuleSet> {

    /**
     * grammar rule: selectorGroup '{' ws declarationList? '}' ws    # knownRuleset
     *
     * @param ctx
     * @return
     */
    @Override
    public RuleSet visitKnownRuleset(CSS3Parser.KnownRulesetContext ctx) {

        var selectors = new SelectorVisitor().visit(ctx.selectorGroup());
        var properties = new ArrayList<Declaration>();
        for (CSS3Parser.DeclarationContext declarationCtx : ctx.declarationList().declaration()) {
            var rule = new PropertyVisitor().visit(declarationCtx);
            if (rule != null) {
                properties.add(rule);
            }
        }

        return new RuleSet(selectors, properties);
    }
}
