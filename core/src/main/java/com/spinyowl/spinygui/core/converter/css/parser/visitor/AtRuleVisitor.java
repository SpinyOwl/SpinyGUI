package com.spinyowl.spinygui.core.converter.css.parser.visitor;

import com.spinyowl.spinygui.core.converter.css.AtRule;
import com.spinyowl.spinygui.core.converter.css.atrule.FontFaceRule;
import com.spinyowl.spinygui.core.converter.css.parser.antlr.CSS3BaseVisitor;
import com.spinyowl.spinygui.core.converter.css.parser.antlr.CSS3Parser.FontFaceDeclarationContext;
import com.spinyowl.spinygui.core.converter.css.parser.antlr.CSS3Parser.FontFaceRuleContext;
import com.spinyowl.spinygui.core.converter.css.parser.antlr.CSS3Parser.KnownFontFaceDeclarationContext;

public class AtRuleVisitor extends CSS3BaseVisitor<AtRule> {

    private FontFaceRule fontFaceRule = null;
    private String fontFamily;
    private String src;

    @Override
    public AtRule visitFontFaceRule(FontFaceRuleContext ctx) {
        for (FontFaceDeclarationContext ffdCtx : ctx.fontFaceDeclaration()) {
            if (ffdCtx instanceof KnownFontFaceDeclarationContext) {
                visitKnownFontFaceDeclaration((KnownFontFaceDeclarationContext) ffdCtx);
            }
        }

        if (fontFamily != null && src != null) {
            fontFaceRule = new FontFaceRule(fontFamily, src);
        }
        return fontFaceRule;
    }

    @Override
    public AtRule visitKnownFontFaceDeclaration(KnownFontFaceDeclarationContext ctx) {
        String name = ctx.property().getText();
        String value = ctx.expr().getText();

        if ("font-family".equalsIgnoreCase(name)) {
            fontFamily = value;
        } else if ("src".equalsIgnoreCase(name)) {
            src = value;
        }

        return super.visitKnownFontFaceDeclaration(ctx);
    }
}
