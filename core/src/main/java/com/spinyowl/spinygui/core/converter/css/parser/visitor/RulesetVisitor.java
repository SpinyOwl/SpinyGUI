package com.spinyowl.spinygui.core.converter.css.parser.visitor;

import com.spinyowl.spinygui.core.converter.css.parser.CSS3BaseVisitor;
import com.spinyowl.spinygui.core.converter.css.parser.CSS3Parser;
import com.spinyowl.spinygui.core.style.css.RuleSet;
import com.spinyowl.spinygui.core.style.css.Property;

import java.util.ArrayList;

public class RulesetVisitor extends CSS3BaseVisitor<RuleSet> {

    /**
     * grammar rule:
     * selectorGroup '{' ws declarationList? '}' ws    # knownRuleset
     *
     * @param ctx
     * @return
     */
    @Override
    public RuleSet visitKnownRuleset(CSS3Parser.KnownRulesetContext ctx) {

        var selectors = new SelectorVisitor().visit(ctx.selectorGroup());
        var properties = new ArrayList<Property>();
        for (CSS3Parser.DeclarationContext declarationCtx : ctx.declarationList().declaration()) {
            var declaration = new PropertyVisitor().visit(declarationCtx);
            if (declaration != null) {
                properties.add(declaration);
            }
        }

        return new RuleSet(selectors, properties);
    }
}
