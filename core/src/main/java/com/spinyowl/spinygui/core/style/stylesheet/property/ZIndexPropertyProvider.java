package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.Z_INDEX;
import static com.spinyowl.spinygui.core.style.stylesheet.Property.isAuto;
import static com.spinyowl.spinygui.core.style.stylesheet.Property.put;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyProvider;
import com.spinyowl.spinygui.core.style.stylesheet.Term;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermInteger;
import java.util.List;

public class ZIndexPropertyProvider implements PropertyProvider {

  private static final Term<?> AUTO = new TermIdent("auto");

  @Override
  public List<Property> getProperties() {
    return List.of(
        Property.builder()
            .name(Z_INDEX)
            .defaultValue(AUTO)
            .updater(put(Z_INDEX, TermIdent.class, v -> 0).or(put(Z_INDEX, TermInteger.class)))
            .validator(isAuto().or(TermInteger.class::isInstance))
            .build());
  }
}
