package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.OPACITY;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermFloat;

public class OpacityProperty extends Property {

  public OpacityProperty() {
    super(
        OPACITY,
        new TermFloat(1f),
        !INHERITABLE,
        ANIMATABLE,
        put(OPACITY, TermFloat.class),
        TermFloat.class::isInstance);
  }
}
