package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.TAB_SIZE;
import static com.spinyowl.spinygui.core.style.stylesheet.Property.put;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyProvider;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermInteger;
import java.util.List;

public class TabSizePropertyProvider implements PropertyProvider {

  @Override
  public List<Property> getProperties() {
    return List.of(
        Property.builder()
            .name(TAB_SIZE)
            .defaultValue(new TermInteger(4))
            .inheritable(true)
            .updater(put(TAB_SIZE, TermInteger.class))
            .validator(TermInteger.class::isInstance)
            .build());
  }
}
