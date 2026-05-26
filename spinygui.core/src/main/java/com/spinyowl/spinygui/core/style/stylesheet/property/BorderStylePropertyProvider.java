package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_BOTTOM_STYLE;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_LEFT_STYLE;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_RIGHT_STYLE;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_STYLE;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_TOP_STYLE;
import static com.spinyowl.spinygui.core.style.stylesheet.Property.checkValue;
import static com.spinyowl.spinygui.core.style.stylesheet.Property.put;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.setOneFour;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyProvider;
import com.spinyowl.spinygui.core.style.stylesheet.Term;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermList;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BorderStylePropertyProvider implements PropertyProvider {

  public static final Term<?> DEFAULT_VALUE = new TermIdent(BorderStyle.NONE.name());

  @Override
  public List<Property> getProperties() {
    return List.of(
        Property.builder()
            .name(BORDER_STYLE)
            .defaultValue(DEFAULT_VALUE)
            .animatable(true)
            .updater(BorderStylePropertyProvider::update)
            .validator(BorderStylePropertyProvider::test)
            .shorthand(true)
            .build(),
        Property.builder()
            .name(BORDER_BOTTOM_STYLE)
            .defaultValue(BorderStylePropertyProvider.DEFAULT_VALUE)
            .animatable(true)
            .updater(put(BORDER_BOTTOM_STYLE, TermIdent.class, BorderStyle::find))
            .validator(checkValue(TermIdent.class, BorderStyle::contains))
            .build(),
        Property.builder()
            .name(BORDER_LEFT_STYLE)
            .defaultValue(BorderStylePropertyProvider.DEFAULT_VALUE)
            .animatable(true)
            .updater(put(BORDER_LEFT_STYLE, TermIdent.class, BorderStyle::find))
            .validator(checkValue(TermIdent.class, BorderStyle::contains))
            .build(),
        Property.builder()
            .name(BORDER_RIGHT_STYLE)
            .defaultValue(BorderStylePropertyProvider.DEFAULT_VALUE)
            .animatable(true)
            .updater(put(BORDER_RIGHT_STYLE, TermIdent.class, BorderStyle::find))
            .validator(checkValue(TermIdent.class, BorderStyle::contains))
            .build(),
        Property.builder()
            .name(BORDER_TOP_STYLE)
            .defaultValue(BorderStylePropertyProvider.DEFAULT_VALUE)
            .animatable(true)
            .updater(put(BORDER_TOP_STYLE, TermIdent.class, BorderStyle::find))
            .validator(checkValue(TermIdent.class, BorderStyle::contains))
            .build());
  }

  private static void update(Term<?> term, Map<String, Object> styles) {
    List<BorderStyle> values = new ArrayList<>();
    if (term instanceof TermIdent termIdent) {
      values.add(BorderStyle.find(termIdent.value()));
    } else if (term instanceof TermList termList) {
      termList
          .terms()
          .forEach(
              t -> {
                if (t instanceof TermIdent termIdent) {
                  values.add(BorderStyle.find(termIdent.value()));
                }
              });
    }
    setOneFour(
        values.toArray(),
        BORDER_TOP_STYLE,
        BORDER_RIGHT_STYLE,
        BORDER_BOTTOM_STYLE,
        BORDER_LEFT_STYLE,
        styles);
  }

  private static boolean test(Term<?> term) {
    return term instanceof TermList termList
        ? termList.terms().stream().allMatch(BorderStylePropertyProvider::testOne)
        : testOne(term);
  }

  public static boolean testOne(Term<?> term) {
    return term instanceof TermIdent termIdent && BorderStyle.contains(termIdent.value());
  }
}
