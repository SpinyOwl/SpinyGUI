package com.spinyowl.spinygui.core.converter.css3.visitor;

import com.spinyowl.spinygui.core.converter.css3.CSS3BaseVisitor;
import com.spinyowl.spinygui.core.converter.css3.CSS3Parser;
import com.spinyowl.spinygui.core.style.RuleSet;
import org.antlr.v4.runtime.tree.ParseTree;

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



        return super.visitKnownRuleset(ctx);
    }
}
