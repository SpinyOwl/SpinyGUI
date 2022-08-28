package com.spinyowl.spinygui.core.style.stylesheet.property.border.radius;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_BOTTOM_LEFT_RADIUS;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_BOTTOM_RIGHT_RADIUS;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_RADIUS;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_TOP_LEFT_RADIUS;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_TOP_RIGHT_RADIUS;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.setOneFour;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.Term;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermLength;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermList;
import com.spinyowl.spinygui.core.style.types.length.Length;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BorderRadiusProperty extends Property {

  private static final ValueExtractor<Length> extractor = ValueExtractors.of(Length.class);

  public BorderRadiusProperty() {
    super(
        BORDER_RADIUS,
        new TermLength(Length.ZERO),
        !INHERITABLE,
        ANIMATABLE,
        BorderRadiusProperty::update,
        BorderRadiusProperty::test,
        true);
  }

  protected static void update(Term<?> term, Map<String, Object> styles) {
    List<Length<?>> values = new ArrayList<>();
    if (term instanceof TermLength termLength) {
      values.add(termLength.value());
    } else if (term instanceof TermList termList) {
      termList
          .terms()
          .forEach(
              t -> {
                if (t instanceof TermLength termLength) {
                  values.add(termLength.value());
                }
              });
    }
    setOneFour(
        values.toArray(),
        BORDER_TOP_LEFT_RADIUS,
        BORDER_TOP_RIGHT_RADIUS,
        BORDER_BOTTOM_RIGHT_RADIUS,
        BORDER_BOTTOM_LEFT_RADIUS,
        styles);
  }

  public static boolean test(Term<?> term) {
    if (term instanceof TermLength) return true;
    if (term instanceof TermList termList) {
      return termList.terms().stream().allMatch(BorderRadiusProperty::test);
    }
    return false;
  }
}
