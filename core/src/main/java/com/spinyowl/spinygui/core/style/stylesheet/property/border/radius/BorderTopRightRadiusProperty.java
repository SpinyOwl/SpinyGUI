package com.spinyowl.spinygui.core.style.stylesheet.property.border.radius;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_TOP_RIGHT_RADIUS;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermLength;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class BorderTopRightRadiusProperty extends Property {

  public BorderTopRightRadiusProperty() {
    super(
        BORDER_TOP_RIGHT_RADIUS,
        new TermLength(Length.ZERO),
        !INHERITABLE,
        ANIMATABLE,
        put(BORDER_TOP_RIGHT_RADIUS, TermLength.class),
        TermLength.class::isInstance);
  }
}
