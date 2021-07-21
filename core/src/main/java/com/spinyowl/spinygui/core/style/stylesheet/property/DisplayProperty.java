package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.DISPLAY;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.types.Display;
import java.util.Map;

public class DisplayProperty extends Property {

  public DisplayProperty() {
    super(
        DISPLAY,
        Display.FLEX.name(),
        INHERITED,
        ANIMATABLE,
        display -> Map.of(DISPLAY, Display.find(display)),
        Display::contains);
  }
}
