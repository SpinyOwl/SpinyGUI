package com.spinyowl.spinygui.core.style.stylesheet.property.flex;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.types.flex.AlignItems;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.ALIGN_ITEMS;

public class AlignItemsProperty extends Property {

  public AlignItemsProperty() {
    super(
        ALIGN_ITEMS,
        "stretch",
        !INHERITED,
        !ANIMATABLE,
        (alignItems, styles) -> styles.put(ALIGN_ITEMS, AlignItems.find(alignItems)),
        AlignItems::contains);
  }
}
