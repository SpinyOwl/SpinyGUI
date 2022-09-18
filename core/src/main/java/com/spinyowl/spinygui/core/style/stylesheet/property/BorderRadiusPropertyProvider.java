package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_BOTTOM_LEFT_RADIUS;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_BOTTOM_RIGHT_RADIUS;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_RADIUS;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_TOP_LEFT_RADIUS;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_TOP_RIGHT_RADIUS;
import static com.spinyowl.spinygui.core.style.stylesheet.Property.put;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.setOneFour;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyProvider;
import com.spinyowl.spinygui.core.style.stylesheet.Term;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermLength;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermList;
import com.spinyowl.spinygui.core.style.types.length.Length;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BorderRadiusPropertyProvider implements PropertyProvider {

  @Override
  public List<Property> getProperties() {
    return List.of(
        Property.builder()
            .name(BORDER_RADIUS)
            .defaultValue(new TermLength(Length.ZERO))
            .animatable(true)
            .updater(BorderRadiusPropertyProvider::update)
            .validator(BorderRadiusPropertyProvider::test)
            .shorthand(true)
            .build(),
        Property.builder()
            .name(BORDER_BOTTOM_LEFT_RADIUS)
            .defaultValue(new TermLength(Length.ZERO))
            .animatable(true)
            .updater(put(BORDER_BOTTOM_LEFT_RADIUS, TermLength.class))
            .validator(TermLength.class::isInstance)
            .build(),
        Property.builder()
            .name(BORDER_BOTTOM_RIGHT_RADIUS)
            .defaultValue(new TermLength(Length.ZERO))
            .animatable(true)
            .updater(put(BORDER_BOTTOM_RIGHT_RADIUS, TermLength.class))
            .validator(TermLength.class::isInstance)
            .build(),
        Property.builder()
            .name(BORDER_TOP_LEFT_RADIUS)
            .defaultValue(new TermLength(Length.ZERO))
            .animatable(true)
            .updater(put(BORDER_TOP_LEFT_RADIUS, TermLength.class))
            .validator(TermLength.class::isInstance)
            .build(),
        Property.builder()
            .name(BORDER_TOP_RIGHT_RADIUS)
            .defaultValue(new TermLength(Length.ZERO))
            .animatable(true)
            .updater(put(BORDER_TOP_RIGHT_RADIUS, TermLength.class))
            .validator(TermLength.class::isInstance)
            .build());
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
      return termList.terms().stream().allMatch(TermLength.class::isInstance);
    }
    return false;
  }
}
