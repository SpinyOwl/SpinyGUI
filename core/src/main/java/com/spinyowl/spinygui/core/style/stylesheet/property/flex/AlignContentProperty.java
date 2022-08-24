package com.spinyowl.spinygui.core.style.stylesheet.property.flex;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.types.flex.AlignContent;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.ALIGN_CONTENT;

public class AlignContentProperty extends Property {

  public AlignContentProperty() {
    super(
        ALIGN_CONTENT,
        "stretch",
        !INHERITABLE,
        !ANIMATABLE,
        (alignContent, styles) -> styles.put(ALIGN_CONTENT, AlignContent.find(alignContent)),
        AlignContent::contains);
  }
}
