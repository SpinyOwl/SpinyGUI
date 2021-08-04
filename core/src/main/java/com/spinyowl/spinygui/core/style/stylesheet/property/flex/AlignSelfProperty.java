package com.spinyowl.spinygui.core.style.stylesheet.property.flex;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.types.flex.AlignSelf;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.ALIGN_SELF;

public class AlignSelfProperty extends Property {

  public AlignSelfProperty() {
    super(
        ALIGN_SELF,
        "auto",
        !INHERITED,
        !ANIMATABLE,
        (alignSelf, styles) -> styles.put(ALIGN_SELF, AlignSelf.find(alignSelf)),
        AlignSelf::contains);
  }
}
