package com.spinyowl.spinygui.core.converter.css3.visitor;

import com.spinyowl.spinygui.core.converter.css3.CSS3BaseVisitor;
import com.spinyowl.spinygui.core.converter.css3.CSS3Parser;
import com.spinyowl.spinygui.core.style.RuleSet;

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

        for (CSS3Parser.DeclarationContext declarationCtx : ctx.declarationList().declaration()) {
            var declaration = new PropertyVisitor().visit(declarationCtx);
            System.out.println(declaration);
        }

        return super.visitKnownRuleset(ctx);
    }
}
