package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.COLOR;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.Term;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermColor;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.types.Color;
import java.util.Map;

public class ColorProperty extends Property {

  public ColorProperty() {
    super(
        COLOR,
        new TermColor(Color.BLACK),
        INHERITABLE,
        ANIMATABLE,
        ColorProperty::extract,
        check(TermIdent.class, Color::exists).or(TermColor.class::isInstance));
  }

  private static void extract(Term<?> term, Map<String, Object> styles) {
    if (term instanceof TermColor termColor) {
      styles.put(COLOR, termColor.value());
    } else if (term instanceof TermIdent termIdent) {
      styles.put(COLOR, Color.get(termIdent.value()));
    }
  }
}
