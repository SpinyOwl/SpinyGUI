package com.spinyowl.spinygui.core.converter.css.parser.visitor;

import com.spinyowl.spinygui.core.converter.css.AtRule;
import com.spinyowl.spinygui.core.converter.css.parser.antlr.CSS3BaseVisitor;
import com.spinyowl.spinygui.core.converter.css.parser.antlr.CSS3Parser.FontFaceDeclarationContext;
import com.spinyowl.spinygui.core.converter.css.parser.antlr.CSS3Parser.FontFaceRuleContext;

public class AtRuleVisitor extends CSS3BaseVisitor<AtRule> {

    @Override
    public AtRule visitFontFaceRule(FontFaceRuleContext ctx) {
        for (FontFaceDeclarationContext ffd : ctx.fontFaceDeclaration()) {

        }

        return super.visitFontFaceRule(ctx);
    }
}
