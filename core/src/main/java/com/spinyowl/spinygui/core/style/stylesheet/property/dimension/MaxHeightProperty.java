package com.spinyowl.spinygui.core.style.stylesheet.property.dimension;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.MAX_HEIGHT;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermLength;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class MaxHeightProperty extends Property {

  public MaxHeightProperty() {
    super(
        MAX_HEIGHT,
        new TermIdent("none"),
        !INHERITABLE,
        ANIMATABLE,
        put(
                MAX_HEIGHT,
                TermIdent.class,
                "none"::equalsIgnoreCase,
                v -> Length.pixel(Integer.MAX_VALUE))
            .or(put(MAX_HEIGHT, TermLength.class)),
        check(TermIdent.class, "none"::equalsIgnoreCase).or(TermLength.class::isInstance));
  }
}
