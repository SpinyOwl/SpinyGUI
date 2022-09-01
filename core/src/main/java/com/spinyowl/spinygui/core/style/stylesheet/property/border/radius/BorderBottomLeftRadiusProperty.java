package com.spinyowl.spinygui.core.style.stylesheet.property.border.radius;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_BOTTOM_LEFT_RADIUS;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermLength;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class BorderBottomLeftRadiusProperty extends Property {

  public BorderBottomLeftRadiusProperty() {
    super(
        BORDER_BOTTOM_LEFT_RADIUS,
        new TermLength(Length.ZERO),
        !INHERITABLE,
        ANIMATABLE,
        put(BORDER_BOTTOM_LEFT_RADIUS, TermLength.class),
        TermLength.class::isInstance);
  }
}
