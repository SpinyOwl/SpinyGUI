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

/** Registers transform properties; function and arbitrary-origin parsing is added in P1/T2. */
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
            .validator(TransformPropertyProvider::isCenter)
            .build());
  }

  private static void updateOrigin(Term<?> term, Map<String, Object> styles) {
    List<Term<?>> values = values(term);
    styles.put(
        TRANSFORM_ORIGIN,
        new TransformOrigin(
            ((TermLength) values.get(0)).value(),
            values.size() == 1 ? Length.percent(0.5f) : ((TermLength) values.get(1)).value()));
  }

  private static boolean isCenter(Term<?> term) {
    List<Term<?>> values = values(term);
    if (values.size() < 1 || values.size() > 2 || values.stream().anyMatch(value -> !(value instanceof TermLength))) {
      return false;
    }
    return true;
  }

  private static List<Term<?>> values(Term<?> term) {
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
    List<Term<?>> arguments = function.terms();
    String name = function.name().toLowerCase();
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
  private static boolean numbers(List<Term<?>> values, int min, int max) { return values.size() >= min && values.size() <= max && values.stream().allMatch(value -> value.value() instanceof Number); }
  private static Length<?> length(List<Term<?>> values, int index) { return ((TermLength) values.get(index)).value(); }
  private static float number(List<Term<?>> values, int index) { return ((Number) values.get(index).value()).floatValue(); }
}
