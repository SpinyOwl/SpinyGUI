package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BACKGROUND_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BACKGROUND_IMAGE;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BACKGROUND_ORIGIN;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BACKGROUND_POSITION;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BACKGROUND_POSITION_X;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BACKGROUND_POSITION_Y;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BACKGROUND_SIZE;
import static com.spinyowl.spinygui.core.style.stylesheet.Property.checkValue;
import static com.spinyowl.spinygui.core.style.stylesheet.Property.put;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.contains;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.indexOf;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.Property.Validator;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyProvider;
import com.spinyowl.spinygui.core.style.stylesheet.Term;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermColor;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermLength;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermList;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermList.Operator;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.background.BackgroundOrigin;
import com.spinyowl.spinygui.core.style.types.background.BackgroundSize;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.style.types.length.Unit;
import java.util.List;
import java.util.Map;

public class BackgroundPropertyProvider implements PropertyProvider {

  private static final String AUTO = "auto";
  private static final String COVER = "cover";
  private static final String CONTAIN = "contain";

  public static final String NONE = "none";
  public static final String EMPTY_STRING = "";

  private static final List<String> values = List.of(AUTO, COVER, CONTAIN);

  private static final List<String> X_VALUES = List.of("left", "center", "right");
  private static final List<String> Y_VALUES = List.of("top", "center", "bottom");

  @Override
  public List<Property> getProperties() {
    return List.of(
        new Property(
            BACKGROUND_SIZE,
            new TermIdent(AUTO),
            false,
            true,
            BackgroundPropertyProvider::updateBackgroundSize,
            backgroundSizeValidator()),
        new Property(
            BACKGROUND_POSITION_Y,
            new TermLength(Length.ZERO),
            false,
            true,
            (term, styles) -> updateBackgroundOnePosition(BACKGROUND_POSITION_Y, term, styles),
            checkValue(TermIdent.class, values::contains).or(TermLength.class::isInstance)),
        new Property(
            BACKGROUND_POSITION_X,
            new TermLength(Length.ZERO),
            false,
            true,
            (term1, styles1) -> updateBackgroundOnePosition(BACKGROUND_POSITION_X, term1, styles1),
            checkValue(TermIdent.class, values::contains).or(TermLength.class::isInstance)),
        new Property(
            BACKGROUND_POSITION,
            new TermList(Operator.SPACE, new TermLength(Length.ZERO), new TermLength(Length.ZERO)),
            false,
            true,
            BackgroundPropertyProvider::updateBackgroundPosition,
            BackgroundPropertyProvider::checkBackgroundPosition,
            true),
        new Property(
            BACKGROUND_ORIGIN,
            new TermIdent(BackgroundOrigin.PADDING_BOX.name()),
            false,
            false,
            put(BACKGROUND_ORIGIN, TermIdent.class, BackgroundOrigin::find),
            checkValue(TermIdent.class, BackgroundOrigin::contains)),
        new Property(
            BACKGROUND_COLOR,
            new TermColor(Color.TRANSPARENT),
            false,
            true,
            (term, styles) -> {
              if (term instanceof TermIdent ti) {
                styles.put(BACKGROUND_COLOR, Color.get(ti.value()));
              } else if (term instanceof TermColor tc) {
                styles.put(BACKGROUND_COLOR, tc.value());
              }
            },
            checkValue(TermIdent.class, Color::exists).or(TermColor.class::isInstance)),
        new Property(
            BACKGROUND_IMAGE,
            new TermIdent(NONE),
            false,
            false,
            (term, styles) -> styles.put(BACKGROUND_IMAGE, extractUrl(((TermIdent) term).value())),
            term -> term instanceof TermIdent ti && test(ti.value())));
  }

  private static Validator backgroundSizeValidator() {
    return checkValue(TermIdent.class, v -> contains(v, values))
        .or(TermLength.class::isInstance)
        .or(
            checkValue(
                TermList.class,
                termList -> {
                  if (termList.isEmpty()) return false;
                  if (termList.size() > 2) return false;
                  if (termList.size() == 1)
                    return checkValue(TermIdent.class, v -> contains(v, values))
                        .or(TermLength.class::isInstance)
                        .test(termList.get(0));
                  return testOne(termList.get(0)) && testOne(termList.get(1));
                }));
  }

  private static boolean testOne(Term<?> term) {
    return checkValue(TermIdent.class, AUTO::equalsIgnoreCase)
        .or(TermLength.class::isInstance)
        .test(term);
  }

  private static void updateBackgroundSize(Term<?> term, Map<String, Object> styles) {
    updateByOneTerm(term, styles);
    if (term instanceof TermList termList) {
      if (termList.size() == 1) {
        updateByOneTerm(termList.get(0), styles);
      } else {
        Unit first = Unit.AUTO;
        Unit second = Unit.AUTO;
        if (termList.get(0) instanceof TermLength termLength) {
          first = termLength.value();
        }
        if (termList.get(1) instanceof TermLength termLength) {
          second = termLength.value();
        }
        styles.put(BACKGROUND_SIZE, BackgroundSize.createSize(first, second));
      }
    }
  }

  private static void updateByOneTerm(Term<?> term, Map<String, Object> styles) {
    if (term instanceof TermIdent termIdent) {
      String value = termIdent.value();
      if (COVER.equalsIgnoreCase(value)) {
        styles.put(BACKGROUND_SIZE, BackgroundSize.createCover());
      } else if (CONTAIN.equalsIgnoreCase(value)) {
        styles.put(BACKGROUND_SIZE, BackgroundSize.createContain());
      } else if (AUTO.equalsIgnoreCase(value)) {
        styles.put(BACKGROUND_SIZE, BackgroundSize.createSize(Unit.AUTO));
      }
    } else if (term instanceof TermLength termLength) {
      styles.put(BACKGROUND_SIZE, BackgroundSize.createSize(termLength.value()));
    }
  }

  private static void updateBackgroundOnePosition(
      String p, Term<?> term, Map<String, Object> styles) {
    if (term instanceof TermLength termLength) {
      styles.put(p, termLength.value());
    } else if (term instanceof TermIdent termIdent && contains(termIdent.value(), values)) {
      styles.put(p, Length.percent(indexOf(termIdent.value(), values) * 50));
    }
  }

  private static boolean checkBackgroundPosition(Term<?> term) {
    if (term instanceof TermLength) return true;
    if (term instanceof TermIdent termIdent)
      return X_VALUES.contains(termIdent.value()) || Y_VALUES.contains(termIdent.value());
    if (term instanceof TermList termList) {
      if (termList.isEmpty() || termList.size() > 2) return false;

      if (termList.size() == 1) {
        return checkValue(TermIdent.class, v -> contains(v, X_VALUES) || contains(v, Y_VALUES))
            .or(TermLength.class::isInstance)
            .test(termList.get(0));
      } else {
        return checkValue(TermIdent.class, v -> contains(v, X_VALUES))
                .or(TermLength.class::isInstance)
                .test(termList.get(0))
            && checkValue(TermIdent.class, v -> contains(v, Y_VALUES))
                .or(TermLength.class::isInstance)
                .test(termList.get(1));
      }
    }
    return false;
  }

  private static void updateBackgroundPosition(Term<?> term, Map<String, Object> styles) {
    if (term instanceof TermList termList) {
      List<Term<?>> terms = termList.value();
      Term<?> term2 = terms.size() > 1 ? terms.get(1) : null;
      styles.put(BACKGROUND_POSITION_X, extractFromTerm(terms.get(0), X_VALUES));
      styles.put(
          BACKGROUND_POSITION_Y,
          term2 == null ? Length.percent(50) : extractFromTerm(term2, Y_VALUES));
    } else if (term instanceof TermIdent termIdent) {
      String value = termIdent.value();
      if (contains(value, X_VALUES)) {
        styles.put(BACKGROUND_POSITION_X, Length.percent(indexOf(value, X_VALUES) * 50F));
        styles.put(BACKGROUND_POSITION_Y, Length.percent(50));
      } else if (contains(value, Y_VALUES)) {
        styles.put(BACKGROUND_POSITION_Y, Length.percent(indexOf(value, Y_VALUES) * 50F));
        styles.put(BACKGROUND_POSITION_X, Length.percent(50F));
      }
    } else if (term instanceof TermLength termLength) {
      styles.put(BACKGROUND_POSITION_X, termLength.value());
      styles.put(BACKGROUND_POSITION_Y, Length.percent(50F));
    }
  }

  private static Length<?> extractFromTerm(Term<?> term, List<String> values) {
    if (term instanceof TermLength termLength) {
      return termLength.value();
    } else if (term instanceof TermIdent termIdent) {
      String value = termIdent.value();
      if (values.contains(value)) {
        return Length.percent(indexOf(value, values) * 50F);
      }
    }
    return Length.ZERO;
  }

  private static boolean test(String value) {
    if (value == null || value.isEmpty()) {
      return false;
    }
    if (NONE.equalsIgnoreCase(value)) {
      return true;
    }
    String trim = value.trim();
    if (!trim.startsWith("url(") || !trim.endsWith(")")) {
      return false;
    }
    var cut = trim.substring(4, trim.length() - 1);

    return escaped(cut, "\"") || escaped(cut, "'") || (!cut.contains("\"") && !cut.contains("'"));
  }

  private static boolean escaped(String cut, String escapeString) {
    return cut.startsWith(escapeString)
        && cut.endsWith(escapeString)
        && cut.replace(escapeString, EMPTY_STRING).length() == cut.length() - 2;
  }

  private static String extractUrl(String value) {
    if (NONE.equals(value)) {
      return EMPTY_STRING;
    }
    String trimmed = value.trim();
    trimmed = trimmed.substring(4, trimmed.length() - 1); // removed 'url(' and ')'
    return trimmed.replace("\"", EMPTY_STRING).replace("'", EMPTY_STRING);
  }
}
