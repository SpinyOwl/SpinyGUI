package com.spinyowl.spinygui.core.style.stylesheet.property.flex;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.ALIGN_ITEMS;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.types.flex.AlignItems;

public class AlignItemsProperty extends Property {

  public AlignItemsProperty() {
    super(
        ALIGN_ITEMS,
        new TermIdent(AlignItems.STRETCH.name()),
        !INHERITABLE,
        !ANIMATABLE,
        put(ALIGN_ITEMS, TermIdent.class, AlignItems::find),
        check(TermIdent.class, AlignItems::contains));
  }
}
