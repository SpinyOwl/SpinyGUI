package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_BOTTOM_WIDTH;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_LEFT_WIDTH;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_RIGHT_WIDTH;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_TOP_WIDTH;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_WIDTH;
import static com.spinyowl.spinygui.core.style.stylesheet.Property.checkValue;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.contains;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyProvider;
import com.spinyowl.spinygui.core.style.stylesheet.Term;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermLength;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermList;
import com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.style.types.length.Length.PixelLength;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BorderWidthPropertyProvider implements PropertyProvider {

  protected static final String THIN = "thin";
  protected static final String MEDIUM = "medium";
  protected static final String THICK = "thick";
  protected static final Term<?> DEFAULT = new TermIdent(MEDIUM);
  protected static final PixelLength THIN_VALUE = Length.pixel(2);
  protected static final PixelLength MEDIUM_VALUE = Length.pixel(4);
  protected static final PixelLength THICK_VALUE = Length.pixel(6);
  protected static final List<String> VALUES = List.of(THIN, MEDIUM, THICK);

  @Override
  public List<Property> getProperties() {
    return List.of(
        new Property(
            BORDER_WIDTH,
            DEFAULT,
            false,
            true,
            BorderWidthPropertyProvider::update,
            checkValue(TermList.class, BorderWidthPropertyProvider::validateList)
                .or(BorderWidthPropertyProvider::validateOne),
            true),
        new Property(
            BORDER_BOTTOM_WIDTH,
            DEFAULT,
            false,
            true,
            (term, styles) -> extractOne(term).ifPresent(l -> styles.put(BORDER_BOTTOM_WIDTH, l)),
            checkValue(TermIdent.class, v -> contains(v, VALUES)).or(TermLength.class::isInstance)),
        new Property(
            BORDER_LEFT_WIDTH,
            DEFAULT,
            false,
            true,
            (term, styles) -> extractOne(term).ifPresent(l -> styles.put(BORDER_LEFT_WIDTH, l)),
            checkValue(TermIdent.class, v -> contains(v, VALUES)).or(TermLength.class::isInstance)),
        new Property(
            BORDER_RIGHT_WIDTH,
            DEFAULT,
            false,
            true,
            (term, styles) -> extractOne(term).ifPresent(l -> styles.put(BORDER_RIGHT_WIDTH, l)),
            checkValue(TermIdent.class, v -> contains(v, VALUES)).or(TermLength.class::isInstance)),
        new Property(
            BORDER_TOP_WIDTH,
            DEFAULT,
            false,
            true,
            (term, styles) -> extractOne(term).ifPresent(l -> styles.put(BORDER_TOP_WIDTH, l)),
            checkValue(TermIdent.class, v -> contains(v, VALUES))
                .or(TermLength.class::isInstance)));
  }

  protected static void update(Term<?> term, Map<String, Object> styles) {
    List<Length<?>> values = new ArrayList<>();
    addOneTermValue(term, values);
    if (term instanceof TermList termList) {
      termList.terms().forEach(t -> addOneTermValue(t, values));
    }
    StyleUtils.setOneFour(
        values.toArray(),
        BORDER_TOP_WIDTH,
        BORDER_RIGHT_WIDTH,
        BORDER_BOTTOM_WIDTH,
        BORDER_LEFT_WIDTH,
        styles);
  }

  private static void addOneTermValue(Term<?> term, List<Length<?>> values) {
    extractOne(term).ifPresent(values::add);
  }

  public static Optional<Length<?>> extractOne(Term<?> term) {
    if (term instanceof TermIdent termIdent) {
      if (THIN.equalsIgnoreCase(termIdent.value())) {
        return Optional.of(THIN_VALUE);
      } else if (MEDIUM.equalsIgnoreCase(termIdent.value())) {
        return Optional.of(MEDIUM_VALUE);
      } else if (THICK.equalsIgnoreCase(termIdent.value())) {
        return Optional.of(THICK_VALUE);
      }
    }
    if (term instanceof TermLength termLength) {
      return Optional.of(termLength.value());
    }
    return Optional.empty();
  }

  private static boolean validateList(List<Term<?>> terms) {
    return terms.stream().allMatch(BorderWidthPropertyProvider::validateOne);
  }

  public static boolean validateOne(Term<?> term) {
    return term instanceof TermIdent termIdent && contains(termIdent.value(), VALUES)
        || term instanceof TermLength;
  }
}
