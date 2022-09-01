package com.spinyowl.spinygui.core.style.stylesheet.property.padding;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.PADDING_TOP;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermLength;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class PaddingTopProperty extends Property {

  public PaddingTopProperty() {
    super(
        PADDING_TOP,
        new TermLength(Length.ZERO),
        !INHERITABLE,
        ANIMATABLE,
        put(PADDING_TOP, TermLength.class),
        TermLength.class::isInstance);
  }
}
