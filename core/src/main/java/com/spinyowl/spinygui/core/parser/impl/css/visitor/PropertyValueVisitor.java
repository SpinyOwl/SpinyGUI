package com.spinyowl.spinygui.core.parser.impl.css.visitor;

import com.spinyowl.spinygui.core.parser.impl.css.antlr.CSS3BaseVisitor;
import com.spinyowl.spinygui.core.parser.impl.css.antlr.CSS3Parser.CalcContext;
import com.spinyowl.spinygui.core.parser.impl.css.antlr.CSS3Parser.DimensionContext;
import com.spinyowl.spinygui.core.parser.impl.css.antlr.CSS3Parser.ExprContext;
import com.spinyowl.spinygui.core.parser.impl.css.antlr.CSS3Parser.Function_Context;
import com.spinyowl.spinygui.core.parser.impl.css.antlr.CSS3Parser.HexcolorContext;
import com.spinyowl.spinygui.core.parser.impl.css.antlr.CSS3Parser.IdentContext;
import com.spinyowl.spinygui.core.parser.impl.css.antlr.CSS3Parser.KnownTermContext;
import com.spinyowl.spinygui.core.parser.impl.css.antlr.CSS3Parser.NumberContext;
import com.spinyowl.spinygui.core.parser.impl.css.antlr.CSS3Parser.PercentageContext;
import com.spinyowl.spinygui.core.parser.impl.css.antlr.CSS3Parser.Var_Context;
import com.spinyowl.spinygui.core.style.stylesheet.Term;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermColor;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermFloat;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermInteger;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermList;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermList.Operator;
import com.spinyowl.spinygui.core.style.types.Color;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PropertyValueVisitor extends CSS3BaseVisitor<Term<?>> {

  private static final String RGB = "rgb(";
  private static final String RGBA = "rgba(";
  private static final String HSL = "hsl(";
  private static final String HSLA = "hsla(";

  private static Operator getOperator(String operatorText) {
    Operator operator;
    if (operatorText.contains(",")) {
      operator = Operator.COMMA;
    } else if (operatorText.contains("/")) {
      operator = Operator.SLASH;
    } else {
      operator = Operator.SPACE;
    }
    return operator;
  }

  @Override
  @SuppressWarnings("squid:S6204")
  public Term<?> visitExpr(ExprContext ctx) {
    log.debug("visitExpr. " + ctx.getText());
    if (ctx.term().size() == 1) return super.visit(ctx.term(0));
    List<Term<?>> terms = ctx.term().stream().map(super::visit).collect(Collectors.toList());
    Operator operator = getOperator(ctx.operator_(0).getText());

    return new TermList(operator, terms);
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
    if (ctx.Minus() != null) {
      log.debug("-" + ctx.Percentage().getText());
    } else {
      log.debug(ctx.Percentage().getText());
    }
    return super.visitPercentage(ctx);
  }

  @Override
  public Term<?> visitVar_(Var_Context ctx) {
    log.debug("visitVar");
    return super.visitVar_(ctx);
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
    String number = ctx.Number().getText();
    if (number.contains(".")) {
      return new TermFloat(Float.parseFloat(number) * (ctx.Minus() != null ? -1 : 1));
    } else {
      return new TermInteger(Integer.parseInt(number) * (ctx.Minus() != null ? -1 : 1));
    }
  }

  @Override
  public Term<?> visitFunction_(Function_Context ctx) {
    log.debug("visitFunction");
    String text = ctx.Function_().getText();
    if (RGB.equalsIgnoreCase(text)) {
      return new TermColor(rgba(ctx, 1F));
    } else if (RGBA.equalsIgnoreCase(text)) {
      return new TermColor(rgba(ctx, childToFloat(ctx, 3)));
    } else if (HSL.equalsIgnoreCase(text)) {
      return new TermColor(hsla(ctx, 1f));
    } else if (HSLA.equalsIgnoreCase(text)) {
      return new TermColor(hsla(ctx, childToFloat(ctx, 3)));
    } else {
      log.warn("Unknown function: " + ctx.getText());
    }
    return super.visitFunction_(ctx);
  }

  private Color hsla(Function_Context ctx, float alpha) {
    return Color.hslToColor(childToInt(ctx, 0), childToInt(ctx, 1), childToInt(ctx, 2), alpha);
  }

  private Color rgba(Function_Context ctx, float alpha) {
    return new Color(childToInt(ctx, 0), childToInt(ctx, 1), childToInt(ctx, 2), alpha);
  }

  private float childToFloat(Function_Context ctx, int i) {
    return Float.parseFloat(ctx.expr().term(i).getText());
  }

  private int childToInt(Function_Context ctx, int i) {
    return Integer.parseInt(ctx.expr().term(i).getText());
  }
}
