package com.spinyowl.spinygui.core.parser.impl.css.parser.visitor;

import com.spinyowl.spinygui.core.font.FontStretch;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3BaseVisitor;
import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3Parser.FontFaceDeclarationContext;
import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3Parser.FontFaceRuleContext;
import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3Parser.KnownFontFaceDeclarationContext;
import com.spinyowl.spinygui.core.style.stylesheet.AtRule;
import com.spinyowl.spinygui.core.style.stylesheet.atrule.FontFaceRule;

public class AtRuleVisitor extends CSS3BaseVisitor<AtRule> {

  public static final String FONT_FAMILY = "font-family";
  public static final String SRC = "src";
  public static final String FONT_STRETCH = "font-stretch";
  public static final String FONT_STYLE = "font-style";
  public static final String FONT_WEIGHT = "font-weight";

  private FontFaceRule fontFaceRule = null;
  private String fontFamily;
  private String src;
  private FontStretch fontStretch;
  private FontStyle fontStyle;
  private FontWeight fontWeight;

  @Override
  public AtRule visitFontFaceRule(FontFaceRuleContext ctx) {
    for (FontFaceDeclarationContext ffdCtx : ctx.fontFaceDeclaration()) {
      if (ffdCtx instanceof KnownFontFaceDeclarationContext fontFace) {
        visitKnownFontFaceDeclaration(fontFace);
      }
    }

    if (fontFamily != null && src != null) {
      fontFaceRule = new FontFaceRule(fontFamily, src);
      fontFaceRule.fontStretch(fontStretch);
      fontFaceRule.fontStyle(fontStyle);
      fontFaceRule.fontWeight(fontWeight);
    }
    return fontFaceRule;
  }

  @Override
  public AtRule visitKnownFontFaceDeclaration(KnownFontFaceDeclarationContext ctx) {
    String name = ctx.property().getText();
    String value = ctx.expr().getText();

    if (FONT_FAMILY.equalsIgnoreCase(name)) {
      fontFamily = value;
    } else if (SRC.equalsIgnoreCase(name)) {
      src = value;
    } else if (FONT_STRETCH.equalsIgnoreCase(name)) {
      fontStretch = FontStretch.find(value);
    } else if (FONT_STYLE.equalsIgnoreCase(name)) {
      fontStyle = FontStyle.find(value);
    } else if (FONT_WEIGHT.equalsIgnoreCase(name)) {
      fontWeight = FontWeight.find(value);
    }

    return super.visitKnownFontFaceDeclaration(ctx);
  }
}
