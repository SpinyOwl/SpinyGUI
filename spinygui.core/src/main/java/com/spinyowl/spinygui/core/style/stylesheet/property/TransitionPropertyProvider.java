package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.TRANSITION;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.TRANSITION_DELAY;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.TRANSITION_DURATION;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.TRANSITION_PROPERTY;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.TRANSITION_TIMING_FUNCTION;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyProvider;
import com.spinyowl.spinygui.core.style.stylesheet.Term;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermFloat;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermFunction;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermInteger;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermList;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermTime;
import com.spinyowl.spinygui.core.style.types.transition.TransitionPropertyName;
import com.spinyowl.spinygui.core.style.types.transition.TransitionPropertySelection;
import com.spinyowl.spinygui.core.style.types.transition.TransitionTime;
import com.spinyowl.spinygui.core.style.types.transition.TransitionTimingFunction;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/** Registers M3's bounded transition longhands and shorthand. */
public class TransitionPropertyProvider implements PropertyProvider {
  private static final TermIdent ALL = new TermIdent("all");
  private static final TermTime ZERO = new TermTime(0d);
  private static final TermIdent EASE = new TermIdent("ease");

  @Override
  public List<Property> getProperties() {
    return List.of(
        Property.builder().name(TRANSITION_PROPERTY).defaultValue(ALL).updater((term, styles) -> styles.put(TRANSITION_PROPERTY, properties(term))).validator(TransitionPropertyProvider::validProperties).build(),
        Property.builder().name(TRANSITION_DURATION).defaultValue(ZERO).updater((term, styles) -> styles.put(TRANSITION_DURATION, times(term))).validator(TransitionPropertyProvider::validTimes).build(),
        Property.builder().name(TRANSITION_DELAY).defaultValue(ZERO).updater((term, styles) -> styles.put(TRANSITION_DELAY, times(term))).validator(TransitionPropertyProvider::validTimes).build(),
        Property.builder().name(TRANSITION_TIMING_FUNCTION).defaultValue(EASE).updater((term, styles) -> styles.put(TRANSITION_TIMING_FUNCTION, timings(term))).validator(TransitionPropertyProvider::validTimings).build(),
        Property.builder().name(TRANSITION).defaultValue(EASE).updater(TransitionPropertyProvider::updateShorthand).validator(TransitionPropertyProvider::validShorthand).shorthand(true).build());
  }

  private static void updateShorthand(Term<?> term, Map<String, Object> styles) {
    List<Term<?>> entries = commaValues(term);
    List<TransitionPropertySelection> properties = new ArrayList<>();
    List<TransitionTime> durations = new ArrayList<>();
    List<TransitionTime> delays = new ArrayList<>();
    List<TransitionTimingFunction> timings = new ArrayList<>();
    for (Term<?> entry : entries) {
      ShorthandEntry parsed = shorthandEntry(entry);
      properties.add(parsed.property()); durations.add(parsed.duration()); delays.add(parsed.delay()); timings.add(parsed.timing());
    }
    styles.put(TRANSITION_PROPERTY, List.copyOf(properties));
    styles.put(TRANSITION_DURATION, compact(durations));
    styles.put(TRANSITION_DELAY, compact(delays));
    styles.put(TRANSITION_TIMING_FUNCTION, compact(timings));
  }

  private static boolean validShorthand(Term<?> term) {
    try { commaValues(term).forEach(TransitionPropertyProvider::shorthandEntry); return true; }
    catch (IllegalArgumentException ignored) { return false; }
  }
  private static ShorthandEntry shorthandEntry(Term<?> entry) {
    List<Term<?>> terms = spaceValues(entry);
    TransitionPropertySelection property = TransitionPropertySelection.ALL;
    TransitionTime duration = TransitionTime.ZERO, delay = TransitionTime.ZERO;
    TransitionTimingFunction timing = TransitionTimingFunction.EASE;
    int timeCount = 0, propertyCount = 0, timingCount = 0;
    for (Term<?> term : terms) {
      Optional<TransitionPropertySelection> selection = selection(term);
      Optional<TransitionTimingFunction> function = timing(term);
      if (term instanceof TermTime time) {
        if (++timeCount == 1) duration = time(time); else if (timeCount == 2) delay = time(time); else throw invalid();
      } else if (selection.isPresent()) {
        if (++propertyCount > 1) throw invalid(); property = selection.get();
      } else if (function.isPresent()) {
        if (++timingCount > 1) throw invalid(); timing = function.get();
      } else throw invalid();
    }
    return new ShorthandEntry(property, duration, timing, delay);
  }
  private static boolean validProperties(Term<?> term) { return commaValues(term).stream().allMatch(value -> selection(value).isPresent()); }
  private static List<TransitionPropertySelection> properties(Term<?> term) { return commaValues(term).stream().map(value -> selection(value).orElseThrow()).toList(); }
  private static Optional<TransitionPropertySelection> selection(Term<?> term) {
    if (!(term instanceof TermIdent ident)) return Optional.empty();
    String value = ident.value().toLowerCase(Locale.ROOT);
    if ("none".equals(value)) return Optional.of(TransitionPropertySelection.NONE);
    if ("all".equals(value)) return Optional.of(TransitionPropertySelection.ALL);
    return TransitionPropertyName.fromCssName(value).map(TransitionPropertySelection.Named::new);
  }
  private static boolean validTimes(Term<?> term) { return commaValues(term).stream().allMatch(value -> value instanceof TermTime time && time.value() >= 0d); }
  private static List<TransitionTime> times(Term<?> term) { return commaValues(term).stream().map(TermTime.class::cast).map(TransitionPropertyProvider::time).toList(); }
  private static TransitionTime time(TermTime time) { return new TransitionTime(time.value()); }
  private static boolean validTimings(Term<?> term) { return commaValues(term).stream().allMatch(value -> timing(value).isPresent()); }
  private static List<TransitionTimingFunction> timings(Term<?> term) { return commaValues(term).stream().map(value -> timing(value).orElseThrow()).toList(); }
  private static Optional<TransitionTimingFunction> timing(Term<?> term) {
    if (term instanceof TermIdent ident) return switch (ident.value().toLowerCase(Locale.ROOT)) {
      case "linear" -> Optional.of(TransitionTimingFunction.Named.LINEAR);
      case "ease" -> Optional.of(TransitionTimingFunction.Named.EASE);
      case "ease-in" -> Optional.of(TransitionTimingFunction.Named.EASE_IN);
      case "ease-out" -> Optional.of(TransitionTimingFunction.Named.EASE_OUT);
      case "ease-in-out" -> Optional.of(TransitionTimingFunction.Named.EASE_IN_OUT);
      default -> Optional.empty();
    };
    if (term instanceof TermFunction function && "cubic-bezier".equalsIgnoreCase(function.name()) && function.size() == 4) {
      try { return Optional.of(new TransitionTimingFunction.CubicBezier(number(function.get(0)), number(function.get(1)), number(function.get(2)), number(function.get(3)))); }
      catch (IllegalArgumentException ignored) { return Optional.empty(); }
    }
    return Optional.empty();
  }
  private static double number(Term<?> term) {
    if (term instanceof TermFloat value) return value.value();
    if (term instanceof TermInteger value) return value.value();
    throw invalid();
  }
  private static List<Term<?>> commaValues(Term<?> term) { return term instanceof TermList list && list.operator() == TermList.Operator.COMMA ? list.terms() : List.of(term); }
  private static List<Term<?>> spaceValues(Term<?> term) { return term instanceof TermList list && list.operator() == TermList.Operator.SPACE ? list.terms() : List.of(term); }
  private static <T> List<T> compact(List<T> values) {
    return values.stream().allMatch(values.getFirst()::equals)
        ? List.of(values.getFirst())
        : List.copyOf(values);
  }
  private static IllegalArgumentException invalid() { return new IllegalArgumentException("invalid transition declaration"); }
  private record ShorthandEntry(TransitionPropertySelection property, TransitionTime duration, TransitionTimingFunction timing, TransitionTime delay) {}
}
