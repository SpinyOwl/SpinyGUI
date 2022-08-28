package com.spinyowl.spinygui.core.style.stylesheet.property.border.radius;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_TOP_LEFT_RADIUS;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermLength;
import com.spinyowl.spinygui.core.style.types.length.Length;
import java.util.function.Function;

public class BorderTopLeftRadiusProperty extends Property {

  public BorderTopLeftRadiusProperty() {
    super(
        BORDER_TOP_LEFT_RADIUS,
        new TermLength(Length.ZERO),
        !INHERITABLE,
        ANIMATABLE,
        put(BORDER_TOP_LEFT_RADIUS, TermLength.class, Function.identity()),
        TermLength.class::isInstance);
  }
}
