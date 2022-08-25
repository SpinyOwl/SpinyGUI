package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.DISPLAY;
import static com.spinyowl.spinygui.core.style.types.Display.BLOCK;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.types.Display;

public class DisplayProperty extends Property {

  public DisplayProperty() {
    super(
        DISPLAY,
        new TermIdent(BLOCK.name()),
        INHERITABLE,
        ANIMATABLE,
        put(DISPLAY, TermIdent.class, Display::find),
        check(TermIdent.class, Display::contains));
  }
}
