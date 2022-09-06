package com.spinyowl.spinygui.core.parser.impl.css.visitor;

import com.spinyowl.spinygui.core.parser.impl.css.antlr.CSS3BaseVisitor;
import com.spinyowl.spinygui.core.parser.impl.css.antlr.CSS3Parser.CalcContext;
import com.spinyowl.spinygui.core.parser.impl.css.antlr.CSS3Parser.DimensionContext;
import com.spinyowl.spinygui.core.parser.impl.css.antlr.CSS3Parser.ExprContext;
import com.spinyowl.spinygui.core.parser.impl.css.antlr.CSS3Parser.Function_Context;
import com.spinyowl.spinygui.core.parser.impl.css.antlr.CSS3Parser.HexcolorContext;
import com.spinyowl.spinygui.core.parser.impl.css.antlr.CSS3Parser.IdentContext;
import com.spinyowl.spinygui.core.parser.impl.css.antlr.CSS3Parser.NumberContext;
import com.spinyowl.spinygui.core.parser.impl.css.antlr.CSS3Parser.Operator_Context;
import com.spinyowl.spinygui.core.parser.impl.css.antlr.CSS3Parser.PercentageContext;
import com.spinyowl.spinygui.core.parser.impl.css.antlr.CSS3Parser.Var_Context;
import com.spinyowl.spinygui.core.style.stylesheet.Term;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermColor;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermFloat;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermFunction;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermInteger;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermLength;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermList;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermList.Operator;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.length.Length;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.tree.RuleNode;
import org.apache.commons.lang3.NotImplementedException;

@Slf4j
public class PropertyValueVisitor extends CSS3BaseVisitor<Term<?>> {

  private static final String RGB = "rgb(";
  private static final String RGBA = "rgba(";
  private static final String HSL = "hsl(";
  private static final String HSLA = "hsla(";

  public static final String PIXEL_REGEX = "-?\\d+[pP][xX]";

  private static Operator getOperator(Operator_Context op) {
    String operatorText = op != null ? op.getText() : " ";
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
    Operator operator = getOperator(ctx.operator_(0));

    return new TermList(operator, terms);
  }

  @Override
  public Term<?> visitHexcolor(HexcolorContext ctx) {
    return new TermColor(Color.fromHex(ctx.getText()));
  }

  @Override
  public Term<?> visitDimension(DimensionContext ctx) {
    String dimensionText = ctx.getText();
    if (dimensionText.matches(PIXEL_REGEX)) {
      return new TermLength(
          Length.pixel(Integer.parseInt(dimensionText.substring(0, dimensionText.length() - 2))));
    }

    throw new NotImplementedException("The only dimension supported is pixel and percentage.");
  }

  @Override
  protected boolean shouldVisitNextChild(RuleNode node, Term<?> currentResult) {
    return currentResult == null;
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
    throw new NotImplementedException("Var is not implemented yet");
  }

  @Override
  public Term<?> visitIdent(IdentContext ctx) {
    log.debug("visitIdent");
    return new TermIdent(ctx.getText());
  }

  @Override
  public Term<?> visitCalc(CalcContext ctx) {
    throw new NotImplementedException("Calc is not implemented yet");
  }

  @Override
  public Term<?> visitNumber(NumberContext ctx) {
    String number = ctx.Number().getText();
    if (number.contains(".")) {
      return new TermFloat(Float.parseFloat(number) * (ctx.Minus() != null ? -1 : 1));
    } else {
      return new TermInteger(Integer.parseInt(number) * (ctx.Minus() != null ? -1 : 1));
    }
  }

  @Override
  @SuppressWarnings("squid:S6204") // Collectors.toList collects to modifiable list.
  public Term<?> visitFunction_(Function_Context ctx) {
    String text = ctx.Function_().getText();
    if (RGB.equalsIgnoreCase(text)) {
      return new TermColor(rgba(ctx, 1F));
    } else if (RGBA.equalsIgnoreCase(text)) {
      return new TermColor(rgba(ctx, childToFloat(ctx, 3)));
    } else if (HSL.equalsIgnoreCase(text)) {
      return new TermColor(hsla(ctx, 1f));
    } else if (HSLA.equalsIgnoreCase(text)) {
      return new TermColor(hsla(ctx, childToFloat(ctx, 3)));
    }

    String name = text.substring(0, text.length() - 1);
    List<Term<?>> args = ctx.expr().term().stream().map(this::visit).collect(Collectors.toList());
    Operator operator = getOperator(ctx.expr().operator_(0));
    return new TermFunction(name, operator, args);
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
