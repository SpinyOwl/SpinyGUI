package com.spinyowl.spinygui.core.style.stylesheet.property;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.types.WhiteSpace;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.WHITE_SPACE;

public class WhiteSpaceProperty extends Property {

  public WhiteSpaceProperty() {
    super(
        WHITE_SPACE,
        WhiteSpace.NORMAL.name(),
        INHERITED,
        !ANIMATABLE,
        (whiteSpace, styles) -> styles.put(WHITE_SPACE, WhiteSpace.find(whiteSpace)),
        WhiteSpace::contains);
  }
}
