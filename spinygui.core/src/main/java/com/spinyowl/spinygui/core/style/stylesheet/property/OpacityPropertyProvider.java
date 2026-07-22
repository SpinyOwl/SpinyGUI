package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.OPACITY;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyProvider;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermFloat;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermInteger;
import java.util.List;

public class OpacityPropertyProvider implements PropertyProvider {

  @Override
  public List<Property> getProperties() {
    return List.of(
        Property.builder()
            .name(OPACITY)
            .defaultValue(new TermFloat(1f))
            .animatable(true)
            .updater((term, styles) -> styles.put(OPACITY, ((Number) term.value()).floatValue()))
            .validator(term -> term instanceof TermFloat || term instanceof TermInteger)
            .build());
  }
}
