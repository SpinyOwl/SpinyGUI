package com.spinyowl.spinygui.core.style.stylesheet.property.padding;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.PADDING_BOTTOM;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermLength;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class PaddingBottomProperty extends Property {

  public PaddingBottomProperty() {
    super(
        PADDING_BOTTOM,
        new TermLength(Length.ZERO),
        !INHERITABLE,
        ANIMATABLE,
        put(PADDING_BOTTOM, TermLength.class),
        TermLength.class::isInstance);
  }
}
