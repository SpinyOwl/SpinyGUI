package com.spinyowl.spinygui.core.style.stylesheet.property.border.width;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_BOTTOM_WIDTH;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_LEFT_WIDTH;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_RIGHT_WIDTH;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_TOP_WIDTH;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_WIDTH;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.contains;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
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

public class BorderWidthProperty extends Property {

  protected static final String THIN = "thin";
  protected static final String MEDIUM = "medium";
  protected static final String THICK = "thick";
  protected static final Term<?> DEFAULT = new TermIdent(MEDIUM);
  protected static final PixelLength THIN_VALUE = Length.pixel(2);
  protected static final PixelLength MEDIUM_VALUE = Length.pixel(4);
  protected static final PixelLength THICK_VALUE = Length.pixel(6);
  protected static final List<String> VALUES = List.of(THIN, MEDIUM, THICK);

  public BorderWidthProperty() {
    super(
        BORDER_WIDTH,
        DEFAULT,
        !INHERITABLE,
        ANIMATABLE,
        BorderWidthProperty::update,
        BorderWidthProperty::test,
        true);
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

  public static boolean test(Term<?> term) {
    return term instanceof TermList termList
        ? termList.terms().stream().allMatch(BorderWidthProperty::testOne)
        : testOne(term);
  }

  public static boolean testOne(Term<?> term) {
    return term instanceof TermIdent termIdent && contains(termIdent.value(), VALUES)
        || term instanceof TermLength;
  }
}
