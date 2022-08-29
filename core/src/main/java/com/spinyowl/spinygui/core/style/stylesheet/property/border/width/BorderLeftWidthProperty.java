package com.spinyowl.spinygui.core.style.stylesheet.property.border.width;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_LEFT_WIDTH;
import static com.spinyowl.spinygui.core.style.stylesheet.property.border.width.BorderWidthProperty.DEFAULT;
import static com.spinyowl.spinygui.core.style.stylesheet.property.border.width.BorderWidthProperty.VALUES;
import static com.spinyowl.spinygui.core.style.stylesheet.property.border.width.BorderWidthProperty.extractOne;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.contains;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermLength;

public class BorderLeftWidthProperty extends Property {

  public BorderLeftWidthProperty() {
    super(
        BORDER_LEFT_WIDTH,
        DEFAULT,
        !INHERITABLE,
        ANIMATABLE,
        (term, styles) -> extractOne(term).ifPresent(l -> styles.put(BORDER_LEFT_WIDTH, l)),
        check(TermIdent.class, v -> contains(v, VALUES)).or(TermLength.class::isInstance));
  }
}
