package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.WHITE_SPACE;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.types.WhiteSpace;
import java.util.Map;

public class WhiteSpaceProperty extends Property {

  public WhiteSpaceProperty() {
    super(
        WHITE_SPACE,
        WhiteSpace.NORMAL.name(),
        INHERITED,
        !ANIMATABLE,
        whiteSpace -> Map.of(WHITE_SPACE, WhiteSpace.find(whiteSpace)),
        WhiteSpace::contains);
  }
}
