package com.spinyowl.spinygui.core.style.stylesheet.property.flex;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FLEX_WRAP;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.flex.FlexWrap;
import com.spinyowl.spinygui.core.style.stylesheet.Property;

public class FlexWrapProperty extends Property<FlexWrap> {

  public FlexWrapProperty() {
    super(
        FLEX_WRAP,
        "nowrap",
        !INHERITED,
        !ANIMATABLE,
        NodeStyle::flexWrap,
        NodeStyle::flexWrap,
        FlexWrap::find,
        FlexWrap::contains);
  }
}
