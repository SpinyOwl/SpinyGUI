package com.spinyowl.spinygui.core.style.stylesheet.property.border.style;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_BOTTOM_STYLE;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_LEFT_STYLE;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_RIGHT_STYLE;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_STYLE;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_TOP_STYLE;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.setOneFour;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.Term;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermList;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BorderStyleProperty extends Property {

  public static final Term<?> DEFAULT_VALUE = new TermIdent(BorderStyle.NONE.name());
  public static final String SPACE_REGEX = "\\s+";

  public BorderStyleProperty() {
    super(
        BORDER_STYLE,
        DEFAULT_VALUE,
        !INHERITABLE,
        ANIMATABLE,
        BorderStyleProperty::update,
        BorderStyleProperty::test,
        true);
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
        ? termList.terms().stream().allMatch(BorderStyleProperty::testOne)
        : testOne(term);
  }

  public static boolean testOne(Term<?> term) {
    return term instanceof TermIdent termIdent && BorderStyle.contains(termIdent.value());
  }
}
