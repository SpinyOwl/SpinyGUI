package com.spinyowl.spinygui.core.converter.css3.visitor;

import com.spinyowl.spinygui.core.converter.css3.CSS3BaseVisitor;
import com.spinyowl.spinygui.core.converter.css3.CSS3Parser;
import com.spinyowl.spinygui.core.style.property.Property;
import com.spinyowl.spinygui.core.style.RuleSet;

import java.util.ArrayList;

public class RulesetVisitor extends CSS3BaseVisitor<RuleSet> {


    @Override
    public RuleSet visitNestedStatement(CSS3Parser.NestedStatementContext ctx) {
        return super.visitNestedStatement(ctx);
    }

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
            properties.add(declaration);
        }

        return new RuleSet(selectors, properties);
    }
}
