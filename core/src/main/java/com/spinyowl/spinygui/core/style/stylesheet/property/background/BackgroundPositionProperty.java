package com.spinyowl.spinygui.core.style.stylesheet.property.background;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BACKGROUND_POSITION;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BACKGROUND_POSITION_X;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BACKGROUND_POSITION_Y;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.Term;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermLength;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermList;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermList.Operator;
import com.spinyowl.spinygui.core.style.types.length.Length;
import java.util.List;
import java.util.Map;

public class BackgroundPositionProperty extends Property {

  private static final List<String> X_VALUES = List.of("left", "center", "right");
  private static final List<String> Y_VALUES = List.of("top", "center", "bottom");

  public BackgroundPositionProperty() {
    super(
        BACKGROUND_POSITION,
        new TermList(Operator.SPACE, new TermLength(Length.ZERO), new TermLength(Length.ZERO)),
        !INHERITABLE,
        ANIMATABLE,
        BackgroundPositionProperty::extract,
        BackgroundPositionProperty::test,
        true);
  }

  private static boolean test(Term<?> term) {
    if (term instanceof TermLength) return true;
    if (term instanceof TermIdent termIdent)
      return X_VALUES.contains(termIdent.value()) || Y_VALUES.contains(termIdent.value());
    if (term instanceof TermList termList) {
      if (termList.isEmpty() || termList.size() > 2) return false;

      if (termList.size() == 1) {
        return check(TermIdent.class, v -> X_VALUES.contains(v) || Y_VALUES.contains(v))
            .or(TermLength.class::isInstance)
            .test(termList.get(0));
      } else {
        return check(TermIdent.class, X_VALUES::contains)
                .or(TermLength.class::isInstance)
                .test(termList.get(0))
            && check(TermIdent.class, Y_VALUES::contains)
                .or(TermLength.class::isInstance)
                .test(termList.get(1));
      }
    }
    return false;
  }

  private static void extract(Term<?> term, Map<String, Object> styles) {
    if (term instanceof TermList termList) {
      List<Term<?>> terms = termList.value();
      Term<?> term2 = terms.size() > 1 ? terms.get(1) : null;
      styles.put(BACKGROUND_POSITION_X, extractFromTerm(terms.get(0), X_VALUES));
      styles.put(
          BACKGROUND_POSITION_Y,
          term2 == null ? Length.percent(50) : extractFromTerm(term2, Y_VALUES));
    } else if (term instanceof TermIdent termIdent) {
      String value = termIdent.value();
      if (X_VALUES.contains(value)) {
        styles.put(
            BACKGROUND_POSITION_X,
            Length.percent(X_VALUES.indexOf(value) * 100f / (X_VALUES.size() - 1)));
        styles.put(BACKGROUND_POSITION_Y, Length.percent(50));
      } else if (Y_VALUES.contains(value)) {
        styles.put(
            BACKGROUND_POSITION_Y,
            Length.percent(Y_VALUES.indexOf(value) * 100f / (Y_VALUES.size() - 1)));
        styles.put(BACKGROUND_POSITION_X, Length.percent(50));
      }
    } else if (term instanceof TermLength termLength) {
      styles.put(BACKGROUND_POSITION_X, termLength.value());
      styles.put(BACKGROUND_POSITION_Y, Length.percent(50));
    }
  }

  private static Length<?> extractFromTerm(Term<?> term, List<String> values) {
    if (term instanceof TermLength termLength) {
      return termLength.value();
    } else if (term instanceof TermIdent termIdent) {
      String value = termIdent.value();
      if (values.contains(value)) {
        return Length.percent(values.indexOf(value) * 100f / (values.size() - 1));
      }
    }
    return Length.ZERO;
  }
}
