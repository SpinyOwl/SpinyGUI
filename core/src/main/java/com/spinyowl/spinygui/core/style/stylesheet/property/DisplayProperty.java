package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.DISPLAY;
import com.spinyowl.spinygui.core.node.style.NodeStyle;
import com.spinyowl.spinygui.core.node.style.types.Display;
import com.spinyowl.spinygui.core.style.stylesheet.Property;

public class DisplayProperty extends Property<Display> {

  public DisplayProperty() {
    super(
        DISPLAY,
        Display.FLEX.name(),
        INHERITED,
        ANIMATABLE,
        NodeStyle::display,
        NodeStyle::display,
        Display::find,
        Display::contains);
  }
}
