package com.spinyowl.spinygui.core.converter.css.property.flex;

import static com.spinyowl.spinygui.core.converter.css.Properties.ALIGN_CONTENT;
import com.spinyowl.spinygui.core.converter.css.model.Property;
import com.spinyowl.spinygui.core.style.types.flex.AlignContent;

public class AlignContentProperty extends Property<AlignContent> {

  public AlignContentProperty() {
    super(ALIGN_CONTENT, "stretch", !INHERITED, !ANIMATABLE,
        (s, v) -> s.flex().alignContent(v), s -> s.flex().alignContent(),
        AlignContent::find, AlignContent::contains);
  }

}
