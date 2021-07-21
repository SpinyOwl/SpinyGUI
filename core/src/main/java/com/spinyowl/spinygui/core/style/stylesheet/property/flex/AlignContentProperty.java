package com.spinyowl.spinygui.core.style.stylesheet.property.flex;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.ALIGN_CONTENT;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.types.flex.AlignContent;
import java.util.Map;

public class AlignContentProperty extends Property {

  public AlignContentProperty() {
    super(
        ALIGN_CONTENT,
        "stretch",
        !INHERITED,
        !ANIMATABLE,
        alignContent -> Map.of(ALIGN_CONTENT, AlignContent.find(alignContent)),
        AlignContent::contains);
  }
}
