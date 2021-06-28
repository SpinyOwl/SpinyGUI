package com.spinyowl.spinygui.core.style.stylesheet.property.flex;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.JUSTIFY_CONTENT;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.types.flex.JustifyContent;

public class JustifyContentProperty extends Property<JustifyContent> {

  public JustifyContentProperty() {
    super(
        JUSTIFY_CONTENT,
        "flex-start",
        !INHERITED,
        !ANIMATABLE,
        NodeStyle::justifyContent,
        NodeStyle::justifyContent,
        JustifyContent::find,
        JustifyContent::contains);
  }
}
