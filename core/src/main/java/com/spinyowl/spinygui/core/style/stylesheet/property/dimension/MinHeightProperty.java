package com.spinyowl.spinygui.core.style.stylesheet.property.dimension;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.MIN_HEIGHT;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermLength;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class MinHeightProperty extends Property {

  public MinHeightProperty() {
    super(
        MIN_HEIGHT,
        new TermLength(Length.ZERO),
        !INHERITABLE,
        ANIMATABLE,
        put(MIN_HEIGHT, TermLength.class),
        TermLength.class::isInstance);
  }
}
