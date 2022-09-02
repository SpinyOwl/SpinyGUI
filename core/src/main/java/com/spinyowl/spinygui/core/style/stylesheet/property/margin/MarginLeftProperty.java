package com.spinyowl.spinygui.core.style.stylesheet.property.margin;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.MARGIN_LEFT;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermLength;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class MarginLeftProperty extends Property {

  public MarginLeftProperty() {
    super(
        MARGIN_LEFT,
        new TermLength(Length.ZERO),
        !INHERITABLE,
        ANIMATABLE,
        put(MARGIN_LEFT, TermLength.class),
        TermLength.class::isInstance);
  }
}
