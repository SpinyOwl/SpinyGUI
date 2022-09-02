package com.spinyowl.spinygui.core.style.stylesheet.property.margin;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.MARGIN_BOTTOM;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermLength;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class MarginBottomProperty extends Property {

  public MarginBottomProperty() {
    super(
        MARGIN_BOTTOM,
        new TermLength(Length.ZERO),
        !INHERITABLE,
        ANIMATABLE,
        put(MARGIN_BOTTOM, TermLength.class),
        TermLength.class::isInstance);
  }
}
