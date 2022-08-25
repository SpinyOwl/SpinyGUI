package com.spinyowl.spinygui.core.style.stylesheet.property.flex;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FLEX_SHRINK;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermFloat;

public class FlexShrinkProperty extends Property {

  public FlexShrinkProperty() {
    super(
        FLEX_SHRINK,
        new TermFloat(0F),
        !INHERITABLE,
        !ANIMATABLE,
        put(FLEX_SHRINK, TermFloat.class, t -> t),
        TermFloat.class::isInstance);
  }
}
