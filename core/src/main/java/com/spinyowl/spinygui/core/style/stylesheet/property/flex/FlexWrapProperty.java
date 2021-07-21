package com.spinyowl.spinygui.core.style.stylesheet.property.flex;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FLEX_WRAP;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.types.flex.FlexWrap;
import java.util.Map;

public class FlexWrapProperty extends Property {

  public FlexWrapProperty() {
    super(
        FLEX_WRAP,
        "nowrap",
        !INHERITED,
        !ANIMATABLE,
        flexWrap -> Map.of(FLEX_WRAP, FlexWrap.find(flexWrap)),
        FlexWrap::contains);
  }
}
