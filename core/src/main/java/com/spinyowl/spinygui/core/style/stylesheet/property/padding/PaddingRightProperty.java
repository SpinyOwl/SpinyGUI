package com.spinyowl.spinygui.core.style.stylesheet.property.padding;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.PADDING_RIGHT;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermLength;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class PaddingRightProperty extends Property {
  public PaddingRightProperty() {
    super(
        PADDING_RIGHT,
        new TermLength(Length.ZERO),
        !INHERITABLE,
        ANIMATABLE,
        put(PADDING_RIGHT, TermLength.class),
        TermLength.class::isInstance);
  }
}
