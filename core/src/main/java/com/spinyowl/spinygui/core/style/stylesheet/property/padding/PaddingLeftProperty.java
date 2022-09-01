package com.spinyowl.spinygui.core.style.stylesheet.property.padding;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.PADDING_LEFT;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermLength;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class PaddingLeftProperty extends Property {
  public PaddingLeftProperty() {
    super(
        PADDING_LEFT,
        new TermLength(Length.ZERO),
        !INHERITABLE,
        ANIMATABLE,
        put(PADDING_LEFT, TermLength.class),
        TermLength.class::isInstance);
  }
}
