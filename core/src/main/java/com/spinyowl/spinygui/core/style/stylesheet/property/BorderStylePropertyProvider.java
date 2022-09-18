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
        new Property(
            BORDER_STYLE,
            DEFAULT_VALUE,
            false,
            true,
            BorderStylePropertyProvider::update,
            BorderStylePropertyProvider::test,
            true),
        new Property(
            BORDER_BOTTOM_STYLE,
            BorderStylePropertyProvider.DEFAULT_VALUE,
            false,
            true,
            put(BORDER_BOTTOM_STYLE, TermIdent.class, BorderStyle::find),
            checkValue(TermIdent.class, BorderStyle::contains)),
        new Property(
            BORDER_LEFT_STYLE,
            BorderStylePropertyProvider.DEFAULT_VALUE,
            false,
            true,
            put(BORDER_LEFT_STYLE, TermIdent.class, BorderStyle::find),
            checkValue(TermIdent.class, BorderStyle::contains)),
        new Property(
            BORDER_RIGHT_STYLE,
            BorderStylePropertyProvider.DEFAULT_VALUE,
            false,
            true,
            put(BORDER_RIGHT_STYLE, TermIdent.class, BorderStyle::find),
            checkValue(TermIdent.class, BorderStyle::contains)),
        new Property(
            BORDER_TOP_STYLE,
            BorderStylePropertyProvider.DEFAULT_VALUE,
            false,
            true,
            put(BORDER_TOP_STYLE, TermIdent.class, BorderStyle::find),
            checkValue(TermIdent.class, BorderStyle::contains)));
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
