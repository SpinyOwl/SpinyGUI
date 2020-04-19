package com.spinyowl.spinygui.core.converter.css.property.flex;

import static com.spinyowl.spinygui.core.converter.css.Properties.ALIGN_ITEMS;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.style.types.flex.AlignItems;

public class AlignItemsProperty extends Property<AlignItems> {

  public AlignItemsProperty() {
    super(ALIGN_ITEMS, "stretch", !INHERITED, !ANIMATABLE,
        (s, v) -> s.flex().alignItems(v), s -> s.flex().alignItems(),
        AlignItems::find, AlignItems::contains);
  }

}
