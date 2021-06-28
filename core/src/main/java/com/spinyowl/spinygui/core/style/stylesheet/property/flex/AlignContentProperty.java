package com.spinyowl.spinygui.core.style.stylesheet.property.flex;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.ALIGN_CONTENT;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.flex.AlignContent;
import com.spinyowl.spinygui.core.style.stylesheet.Property;

public class AlignContentProperty extends Property<AlignContent> {

  public AlignContentProperty() {
    super(
        ALIGN_CONTENT,
        "stretch",
        !INHERITED,
        !ANIMATABLE,
        NodeStyle::alignContent,
        NodeStyle::alignContent,
        AlignContent::find,
        AlignContent::contains);
  }
}
