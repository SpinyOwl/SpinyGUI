package com.spinyowl.spinygui.core.style.stylesheet.property.flex;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.ALIGN_ITEMS;
import com.spinyowl.spinygui.core.style.types.flex.AlignItems;
import com.spinyowl.spinygui.core.style.stylesheet.Property;

public class AlignItemsProperty extends Property<AlignItems> {

  public AlignItemsProperty() {
    super(
        ALIGN_ITEMS,
        "stretch",
        !INHERITED,
        !ANIMATABLE,
        (s, v) -> s.flex().alignItems(v),
        s -> s.flex().alignItems(),
        AlignItems::find,
        AlignItems::contains);
  }
}
