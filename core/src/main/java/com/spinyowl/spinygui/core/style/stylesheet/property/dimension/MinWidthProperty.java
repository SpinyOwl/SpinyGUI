package com.spinyowl.spinygui.core.style.stylesheet.property.dimension;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.MIN_WIDTH;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermLength;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class MinWidthProperty extends Property {

  public MinWidthProperty() {
    super(
        MIN_WIDTH,
        new TermLength(Length.ZERO),
        !INHERITABLE,
        ANIMATABLE,
        put(MIN_WIDTH, TermLength.class),
        TermLength.class::isInstance);
  }
}
