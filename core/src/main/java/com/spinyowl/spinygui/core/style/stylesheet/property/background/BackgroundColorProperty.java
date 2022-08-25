package com.spinyowl.spinygui.core.style.stylesheet.property.background;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BACKGROUND_COLOR;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermColor;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.types.Color;

public class BackgroundColorProperty extends Property {

  public BackgroundColorProperty() {
    super(
        BACKGROUND_COLOR,
        new TermColor(Color.TRANSPARENT),
        !INHERITABLE,
        ANIMATABLE,
        (term, styles) -> {
          if (term instanceof TermIdent ti) {
            styles.put(BACKGROUND_COLOR, Color.get(ti.value()));
          } else if (term instanceof TermColor tc) {
            styles.put(BACKGROUND_COLOR, tc.value());
          }
        },
        check(TermIdent.class, Color::exists).or(TermColor.class::isInstance));
  }
}
