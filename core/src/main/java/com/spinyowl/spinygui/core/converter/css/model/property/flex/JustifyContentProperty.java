package com.spinyowl.spinygui.core.converter.css.model.property.flex;

import static com.spinyowl.spinygui.core.converter.css.Properties.JUSTIFY_CONTENT;
import com.spinyowl.spinygui.core.converter.css.model.Property;
import com.spinyowl.spinygui.core.style.types.flex.JustifyContent;

public class JustifyContentProperty extends Property<JustifyContent> {

  public JustifyContentProperty() {
    super(JUSTIFY_CONTENT, "flex-start", !INHERITED, !ANIMATABLE,
        (s, v) -> s.flex().justifyContent(v), s -> s.flex().justifyContent(),
        JustifyContent::find, JustifyContent::contains);
  }
}
