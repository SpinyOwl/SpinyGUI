package com.spinyowl.spinygui.core.style.stylesheet.property.flex;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.ALIGN_SELF;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.types.flex.AlignSelf;
import java.util.Map;

public class AlignSelfProperty extends Property {

  public AlignSelfProperty() {
    super(
        ALIGN_SELF,
        "auto",
        !INHERITED,
        !ANIMATABLE,
        alignSelf -> Map.of(ALIGN_SELF, AlignSelf.find(alignSelf)),
        AlignSelf::contains);
  }
}
