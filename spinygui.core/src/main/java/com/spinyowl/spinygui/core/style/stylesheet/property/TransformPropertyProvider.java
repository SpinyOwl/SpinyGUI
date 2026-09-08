package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.TRANSFORM;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.TRANSFORM_ORIGIN;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyProvider;
import com.spinyowl.spinygui.core.style.stylesheet.Term;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermAngle;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermFunction;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermLength;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermList;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermList.Operator;
import com.spinyowl.spinygui.core.style.types.Transform;
import com.spinyowl.spinygui.core.style.types.TransformOrigin;
import com.spinyowl.spinygui.core.style.types.length.Length;
import java.util.List;
import java.util.Map;

/** Registers supported 2D transforms and axis-validated length/keyword origins. */
public class TransformPropertyProvider implements PropertyProvider {

  private static final TermIdent NONE = new TermIdent("none");
  private static final TermList CENTER =
      new TermList(
          Operator.SPACE, new TermLength(Length.percent(0.5f)), new TermLength(Length.percent(0.5f)));

  @Override
  public List<Property> getProperties() {
    return List.of(
        Property.builder()
            .name(TRANSFORM)
            .defaultValue(NONE)
            .updater(TransformPropertyProvider::updateTransform)
            .validator(TransformPropertyProvider::isTransform)
            .build(),
        Property.builder()
            .name(TRANSFORM_ORIGIN)
            .defaultValue(CENTER)
            .updater(TransformPropertyProvider::updateOrigin)
            .validator(term -> parseOrigin(term) != null)
            .build());
  }

  private static void updateOrigin(Term<?> term, Map<String, Object> styles) {
    styles.put(TRANSFORM_ORIGIN, parseOrigin(term));
  }

  private static TransformOrigin parseOrigin(Term<?> term) {
    List<Term<?>> values = values(term);
    if (values.size() == 1) {
      Length<?> x = originAxis(values.getFirst(), true);
      if (x != null) return new TransformOrigin(x, Length.percent(.5f));
      Length<?> y = originAxis(values.getFirst(), false);
      return y == null ? null : new TransformOrigin(Length.percent(.5f), y);
    }
    if (values.size() != 2) return null;
    Length<?> x = originAxis(values.get(0), true);
    Length<?> y = originAxis(values.get(1), false);
    if (x != null && y != null) return new TransformOrigin(x, y);
    // Only keyword pairs may reverse axis order; lengths stay in x/y order.
    if (values.get(0) instanceof TermIdent && values.get(1) instanceof TermIdent) {
      x = originAxis(values.get(1), true);
      y = originAxis(values.get(0), false);
      if (x != null && y != null) return new TransformOrigin(x, y);
    }
    return null;
  }

  private static Length<?> originAxis(Term<?> term, boolean horizontal) {
    if (term instanceof TermLength length) return length.value();
    if (!(term instanceof TermIdent ident)) return null;
    return switch (ident.value().toLowerCase(java.util.Locale.ROOT)) {
      case "center" -> Length.percent(.5f);
      case "left" -> horizontal ? Length.ZERO : null;
      case "right" -> horizontal ? Length.percent(1) : null;
      case "top" -> horizontal ? null : Length.ZERO;
      case "bottom" -> horizontal ? null : Length.percent(1);
      default -> null;
    };
  }

  private static List<Term<?>> values(Term<?> term) {
    if (term instanceof TermFunction) return List.of(term);
    if (term instanceof TermList terms && Operator.SPACE.equals(terms.operator())) return terms.terms();
    return List.of(term);
  }

  private static boolean isTransform(Term<?> term) {
    if (term instanceof TermIdent ident) return "none".equalsIgnoreCase(ident.value());
    return values(term).stream().allMatch(TransformPropertyProvider::operation) && !values(term).isEmpty();
  }

  private static boolean operation(Term<?> term) {
    if (!(term instanceof TermFunction function)) return false;
    return parse(function) != null;
  }

  private static void updateTransform(Term<?> term, Map<String, Object> styles) {
    if (term instanceof TermIdent) {
      styles.put(TRANSFORM, Transform.NONE);
      return;
    }
    styles.put(TRANSFORM, new Transform.Operations(values(term).stream().map(TermFunction.class::cast).map(TransformPropertyProvider::parse).toList()));
  }

  private static Transform parse(TermFunction function) {
    String name = function.name().toLowerCase();
    List<Term<?>> arguments =
        function.terms().stream()
            .filter(term -> !(term instanceof TermIdent ident && name.equalsIgnoreCase(ident.value())))
            .toList();
    return switch (name) {
      case "translate" -> lengths(arguments, 1, 2) ? new Transform.Translate(length(arguments, 0), arguments.size() == 1 ? Length.ZERO : length(arguments, 1)) : null;
      case "translatex" -> lengths(arguments, 1, 1) ? new Transform.Translate(length(arguments, 0), Length.ZERO) : null;
      case "translatey" -> lengths(arguments, 1, 1) ? new Transform.Translate(Length.ZERO, length(arguments, 0)) : null;
      case "scale" -> numbers(arguments, 1, 2) ? new Transform.Scale(number(arguments, 0), arguments.size() == 1 ? number(arguments, 0) : number(arguments, 1)) : null;
      case "scalex" -> numbers(arguments, 1, 1) ? new Transform.Scale(number(arguments, 0), 1f) : null;
      case "scaley" -> numbers(arguments, 1, 1) ? new Transform.Scale(1f, number(arguments, 0)) : null;
      case "rotate" -> arguments.size() == 1 && arguments.getFirst() instanceof TermAngle angle ? new Transform.Rotate(angle.value()) : null;
      default -> null;
    };
  }

  private static boolean lengths(List<Term<?>> values, int min, int max) { return values.size() >= min && values.size() <= max && values.stream().allMatch(TermLength.class::isInstance); }
  private static boolean numbers(List<Term<?>> values, int min, int max) { return values.size() >= min && values.size() <= max && values.stream().allMatch(value -> value.value() instanceof Number || numericIdent(value)); }
  private static Length<?> length(List<Term<?>> values, int index) { return ((TermLength) values.get(index)).value(); }
  private static float number(List<Term<?>> values, int index) {
    Term<?> value = values.get(index);
    return value.value() instanceof Number number
        ? number.floatValue()
        : Float.parseFloat(((TermIdent) value).value());
  }

  private static boolean numericIdent(Term<?> value) {
    if (!(value instanceof TermIdent ident)) {
      return false;
    }
    try {
      Float.parseFloat(ident.value());
      return true;
    } catch (NumberFormatException ignored) {
      return false;
    }
  }
}
