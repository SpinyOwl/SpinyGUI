package com.spinyowl.spinygui.core.style.stylesheet.property.margin;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.MARGIN_RIGHT;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermLength;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class MarginRightProperty extends Property {

  public MarginRightProperty() {
    super(
        MARGIN_RIGHT,
        new TermLength(Length.ZERO),
        !INHERITABLE,
        ANIMATABLE,
        put(MARGIN_RIGHT, TermLength.class),
        TermLength.class::isInstance);
  }
}
