package com.spinyowl.spinygui.core.style.stylesheet.property.margin;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.MARGIN_TOP;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermLength;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class MarginTopProperty extends Property {

  public MarginTopProperty() {
    super(
        MARGIN_TOP,
        new TermLength(Length.ZERO),
        !INHERITABLE,
        ANIMATABLE,
        put(MARGIN_TOP, TermLength.class),
        TermLength.class::isInstance);
  }
}
