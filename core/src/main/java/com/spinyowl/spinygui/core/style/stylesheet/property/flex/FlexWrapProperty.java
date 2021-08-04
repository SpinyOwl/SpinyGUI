package com.spinyowl.spinygui.core.style.stylesheet.property.flex;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.types.flex.FlexWrap;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FLEX_WRAP;

public class FlexWrapProperty extends Property {

  public FlexWrapProperty() {
    super(
        FLEX_WRAP,
        "nowrap",
        !INHERITED,
        !ANIMATABLE,
        (flexWrap, styles) -> styles.put(FLEX_WRAP, FlexWrap.find(flexWrap)),
        FlexWrap::contains);
  }
}
