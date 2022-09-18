package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_BOTTOM_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_LEFT_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_RIGHT_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_TOP_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Property.checkValue;
import static com.spinyowl.spinygui.core.style.stylesheet.Property.put;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyProvider;
import com.spinyowl.spinygui.core.style.stylesheet.Term;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermColor;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermList;
import com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils;
import com.spinyowl.spinygui.core.style.types.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BorderColorPropertyProvider implements PropertyProvider {

  public static final Term<?> DEFAULT_VALUE = new TermIdent("black");

  protected static void update(Term<?> term, Map<String, Object> styles) {
    List<Color> colors = new ArrayList<>();
    extractOne(term).ifPresent(colors::add);
    if (term instanceof TermList termList) {
      termList.terms().forEach(t -> extractOne(term).ifPresent(colors::add));
    }

    StyleUtils.setOneFour(
        colors.toArray(),
        BORDER_TOP_COLOR,
        BORDER_RIGHT_COLOR,
        BORDER_BOTTOM_COLOR,
        BORDER_LEFT_COLOR,
        styles);
  }

  public static Optional<Color> extractOne(Term<?> term) {
    if (term instanceof TermIdent termIdent) {
      return Optional.of(Color.get(termIdent.value()));
    } else if (term instanceof TermColor termColor) {
      return Optional.of(termColor.value());
    }
    return Optional.empty();
  }

  private static boolean test(Term<?> term) {
    return term instanceof TermList termList
        ? termList.terms().stream().allMatch(BorderColorPropertyProvider::testOne)
        : testOne(term);
  }

  public static boolean testOne(Term<?> term) {
    return (term instanceof TermIdent ti && Color.exists(ti.value())) || term instanceof TermColor;
  }

  @Override
  public List<Property> getProperties() {
    return List.of(
        Property.builder()
            .name(BORDER_COLOR)
            .defaultValue(DEFAULT_VALUE)
            .animatable(true)
            .updater(BorderColorPropertyProvider::update)
            .validator(BorderColorPropertyProvider::test)
            .shorthand(true)
            .build(),
        Property.builder()
            .name(BORDER_BOTTOM_COLOR)
            .defaultValue(BorderColorPropertyProvider.DEFAULT_VALUE)
            .animatable(true)
            .updater(
                put(BORDER_BOTTOM_COLOR, TermIdent.class, Color::get)
                    .or(put(BORDER_BOTTOM_COLOR, TermColor.class)))
            .validator(checkValue(TermIdent.class, Color::exists).or(TermColor.class::isInstance))
            .build(),
        Property.builder()
            .name(BORDER_LEFT_COLOR)
            .defaultValue(BorderColorPropertyProvider.DEFAULT_VALUE)
            .animatable(true)
            .updater(
                put(BORDER_LEFT_COLOR, TermIdent.class, Color::get)
                    .or(put(BORDER_LEFT_COLOR, TermColor.class)))
            .validator(checkValue(TermIdent.class, Color::exists).or(TermColor.class::isInstance))
            .build(),
        Property.builder()
            .name(BORDER_RIGHT_COLOR)
            .defaultValue(BorderColorPropertyProvider.DEFAULT_VALUE)
            .animatable(true)
            .updater(
                put(BORDER_RIGHT_COLOR, TermIdent.class, Color::get)
                    .or(put(BORDER_RIGHT_COLOR, TermColor.class)))
            .validator(checkValue(TermIdent.class, Color::exists).or(TermColor.class::isInstance))
            .build(),
        Property.builder()
            .name(BORDER_TOP_COLOR)
            .defaultValue(BorderColorPropertyProvider.DEFAULT_VALUE)
            .animatable(true)
            .updater(
                put(BORDER_TOP_COLOR, TermIdent.class, Color::get)
                    .or(put(BORDER_TOP_COLOR, TermColor.class)))
            .validator(checkValue(TermIdent.class, Color::exists).or(TermColor.class::isInstance))
            .build());
  }
}
