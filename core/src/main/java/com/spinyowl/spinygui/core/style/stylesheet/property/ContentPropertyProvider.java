package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.CONTENT;
import static com.spinyowl.spinygui.core.style.stylesheet.Property.put;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyProvider;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import java.util.List;

public class ContentPropertyProvider implements PropertyProvider {

  @Override
  public List<Property> getProperties() {
    return List.of(
        Property.builder()
            .name(CONTENT)
            .defaultValue(new TermIdent(""))
            .updater(put(CONTENT, TermIdent.class))
            .validator(TermIdent.class::isInstance)
            .inheritable(false)
            .animatable(false)
            .build());
  }
}
