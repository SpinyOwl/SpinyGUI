package com.spinyowl.spinygui.core.style.stylesheet.property;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.types.Display;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.DISPLAY;

public class DisplayProperty extends Property {

  public DisplayProperty() {
    super(
        DISPLAY,
        Display.BLOCK.name(),
        INHERITED,
        ANIMATABLE,
        (display, styles) -> styles.put(DISPLAY, Display.find(display)),
        Display::contains);
  }
}
