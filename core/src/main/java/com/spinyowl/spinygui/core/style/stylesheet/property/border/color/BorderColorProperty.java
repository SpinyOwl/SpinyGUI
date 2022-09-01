package com.spinyowl.spinygui.core.style.stylesheet.property.border.color;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_BOTTOM_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_LEFT_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_RIGHT_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_TOP_COLOR;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
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

public class BorderColorProperty extends Property {

  public static final Term<?> DEFAULT_VALUE = new TermIdent("black");

  public BorderColorProperty() {
    super(
        BORDER_COLOR,
        DEFAULT_VALUE,
        !INHERITABLE,
        ANIMATABLE,
        BorderColorProperty::update,
        BorderColorProperty::test,
        true);
  }

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
        ? termList.terms().stream().allMatch(BorderColorProperty::testOne)
        : testOne(term);
  }

  public static boolean testOne(Term<?> term) {
    return (term instanceof TermIdent ti && Color.exists(ti.value())) || term instanceof TermColor;
  }
}
