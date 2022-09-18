package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.DISPLAY;
import static com.spinyowl.spinygui.core.style.stylesheet.Property.checkValue;
import static com.spinyowl.spinygui.core.style.stylesheet.Property.put;
import static com.spinyowl.spinygui.core.style.types.Display.BLOCK;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyProvider;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.types.Display;
import java.util.List;

public class DisplayPropertyProvider implements PropertyProvider {

  @Override
  public List<Property> getProperties() {
    return List.of(
        new Property(
            DISPLAY,
            new TermIdent(BLOCK.name()),
            true,
            true,
            put(DISPLAY, TermIdent.class, Display::find),
            checkValue(TermIdent.class, Display::contains)));
  }
}
