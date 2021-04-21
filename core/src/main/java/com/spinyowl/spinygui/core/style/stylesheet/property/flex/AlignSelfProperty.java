package com.spinyowl.spinygui.core.style.stylesheet.property.flex;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.ALIGN_SELF;
import com.spinyowl.spinygui.core.node.style.types.flex.AlignSelf;
import com.spinyowl.spinygui.core.style.stylesheet.Property;

public class AlignSelfProperty extends Property<AlignSelf> {

  public AlignSelfProperty() {
    super(
        ALIGN_SELF,
        "auto",
        !INHERITED,
        !ANIMATABLE,
        (s, v) -> s.flex().alignSelf(v),
        s -> s.flex().alignSelf(),
        AlignSelf::find,
        AlignSelf::contains);
  }
}
