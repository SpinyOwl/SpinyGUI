package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.WHITE_SPACE;
import static com.spinyowl.spinygui.core.style.stylesheet.Property.checkValue;
import static com.spinyowl.spinygui.core.style.stylesheet.Property.put;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyProvider;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.types.WhiteSpace;
import java.util.List;

public class WhiteSpacePropertyProvider implements PropertyProvider {

  @Override
  public List<Property> getProperties() {
    return List.of(
        Property.builder()
            .name(WHITE_SPACE)
            .defaultValue(new TermIdent(WhiteSpace.NORMAL.name()))
            .inheritable(true)
            .updater(put(WHITE_SPACE, TermIdent.class, WhiteSpace::find))
            .validator(checkValue(TermIdent.class, WhiteSpace::contains))
            .build());
  }
}
