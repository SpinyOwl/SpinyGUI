package com.spinyowl.spinygui.core.style.stylesheet.property.flex;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FLEX_GROW;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermFloat;

public class FlexGrowProperty extends Property {

  public FlexGrowProperty() {
    super(
        FLEX_GROW,
        new TermFloat(0F),
        !INHERITABLE,
        !ANIMATABLE,
        put(FLEX_GROW, TermFloat.class, t -> t),
        TermFloat.class::isInstance);
  }
}
