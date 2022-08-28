package com.spinyowl.spinygui.core.style.stylesheet.property.background;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BACKGROUND_POSITION_X;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.Term;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermLength;
import com.spinyowl.spinygui.core.style.types.length.Length;
import java.util.List;
import java.util.Map;

public class BackgroundPositionXProperty extends Property {

  private static final List<String> values = List.of("left", "center", "right");

  public BackgroundPositionXProperty() {
    super(
        BACKGROUND_POSITION_X,
        new TermLength(Length.ZERO),
        !INHERITABLE,
        ANIMATABLE,
        BackgroundPositionXProperty::update,
        check(TermIdent.class, values::contains).or(TermLength.class::isInstance));
  }

  private static void update(Term<?> term, Map<String, Object> styles) {
    if (term instanceof TermLength termLength) {
      styles.put(BACKGROUND_POSITION_X, termLength.value());
    } else if (term instanceof TermIdent termIdent && values.contains(termIdent.value())) {
      styles.put(BACKGROUND_POSITION_X, Length.percent(values.indexOf(termIdent.value()) * 50));
    }
  }
}
