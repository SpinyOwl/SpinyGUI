package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Property.checkValue;
import static com.spinyowl.spinygui.core.style.stylesheet.Property.put;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyProvider;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermColor;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.types.Color;
import java.util.List;

public class ColorPropertyProvider implements PropertyProvider {

  @Override
  public List<Property> getProperties() {
    return List.of(
        Property.builder()
            .name(COLOR)
            .defaultValue(new TermColor(Color.BLACK))
            .inheritable(true)
            .animatable(true)
            .updater(put(COLOR, TermIdent.class, Color::get).or(put(COLOR, TermColor.class)))
            .validator(checkValue(TermIdent.class, Color::exists).or(TermColor.class::isInstance))
            .build());
  }
}
