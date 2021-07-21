package com.spinyowl.spinygui.core.style.stylesheet.property.flex;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.ALIGN_ITEMS;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.types.flex.AlignItems;
import java.util.Map;

public class AlignItemsProperty extends Property {

  public AlignItemsProperty() {
    super(
        ALIGN_ITEMS,
        "stretch",
        !INHERITED,
        !ANIMATABLE,
        alignItems -> Map.of(ALIGN_ITEMS, AlignItems.find(alignItems)),
        AlignItems::contains);
  }
}
