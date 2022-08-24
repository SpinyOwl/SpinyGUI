package com.spinyowl.spinygui.core.parser.impl.css.parser.visitor;

import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3BaseVisitor;
import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3Parser.CalcContext;
import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3Parser.DimensionContext;
import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3Parser.ExprContext;
import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3Parser.FunctionContext;
import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3Parser.HexcolorContext;
import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3Parser.IdentContext;
import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3Parser.KnownTermContext;
import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3Parser.NumberContext;
import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3Parser.PercentageContext;
import com.spinyowl.spinygui.core.parser.impl.css.parser.antlr.CSS3Parser.VarContext;
import com.spinyowl.spinygui.core.style.stylesheet.Term;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermColor;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermFloat;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermList;
import com.spinyowl.spinygui.core.style.types.Color;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PropertyValueVisitor extends CSS3BaseVisitor<Term<?>> {

  private static final String RGB = "rgb(";
  private static final String RGBA = "rgba(";
  private static final String HSL = "hsl(";
  private static final String HSLA = "hsla(";

  @Override
  public Term<?> visitExpr(ExprContext ctx) {
    log.debug("visitExpr. " + ctx.getText());
    if (ctx.term().size() == 1) return super.visit(ctx.term(0));
    return new TermList(ctx.term().stream().map(super::visit).collect(Collectors.toList()));
  }

  @Override
  public Term<?> visitHexcolor(HexcolorContext ctx) {
    log.debug("visitHexcolor");
    return new TermColor(Color.fromHex(ctx.getText()));
  }

  @Override
  public Term<?> visitKnownTerm(KnownTermContext ctx) {
    log.debug("visitKnownTerm");
    return super.visitKnownTerm(ctx);
  }

  @Override
  public Term<?> visitDimension(DimensionContext ctx) {
    log.debug("visitDimension");

    return super.visitDimension(ctx);
  }

  @Override
  public Term<?> visitPercentage(PercentageContext ctx) {
    log.debug("visitPercentage");
    return super.visitPercentage(ctx);
  }

  @Override
  public Term<?> visitVar(VarContext ctx) {
    log.debug("visitVar");
    return super.visitVar(ctx);
  }

  @Override
  public Term<?> visitIdent(IdentContext ctx) {
    log.debug("visitIdent");
    return new TermIdent(ctx.getText());
  }

  @Override
  public Term<?> visitCalc(CalcContext ctx) {
    log.debug("visitCalc");
    return super.visitCalc(ctx);
  }

  @Override
  public Term<?> visitNumber(NumberContext ctx) {
    log.debug("visitNumber");
    float value = Float.parseFloat(ctx.Number().getText());
    return new TermFloat(ctx.Minus() == null ? value : -value);
  }

  @Override
  public Term<?> visitFunction(FunctionContext ctx) {
    log.debug("visitFunction");
    String text = ctx.Function().getText();
    if (RGB.equalsIgnoreCase(text)) {
      return new TermColor(rgba(ctx, 1F));
    } else if (RGBA.equalsIgnoreCase(text)) {
      return new TermColor(rgba(ctx, childToFloat(ctx, 3)));
    } else if (HSL.equalsIgnoreCase(text)) {
      return new TermColor(hsla(ctx, 1f));
    } else if (HSLA.equalsIgnoreCase(text)) {
      return new TermColor(hsla(ctx, childToFloat(ctx, 3)));
    }
    return super.visitFunction(ctx);
  }

  private Color hsla(FunctionContext ctx, float alpha) {
    return Color.hslToColor(childToInt(ctx, 0), childToInt(ctx, 1), childToInt(ctx, 2), alpha);
  }

  private Color rgba(FunctionContext ctx, float alpha) {
    return new Color(childToInt(ctx, 0), childToInt(ctx, 1), childToInt(ctx, 2), alpha);
  }

  private float childToFloat(FunctionContext ctx, int i) {
    return Float.parseFloat(ctx.expr().term(i).getText());
  }

  private int childToInt(FunctionContext ctx, int i) {
    return Integer.parseInt(ctx.expr().term(i).getText());
  }
}
