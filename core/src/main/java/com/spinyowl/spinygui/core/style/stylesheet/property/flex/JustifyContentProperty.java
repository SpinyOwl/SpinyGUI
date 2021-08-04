package com.spinyowl.spinygui.core.style.stylesheet.property.flex;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.types.flex.JustifyContent;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.JUSTIFY_CONTENT;

public class JustifyContentProperty extends Property {

  public JustifyContentProperty() {
    super(
        JUSTIFY_CONTENT,
        "flex-start",
        !INHERITED,
        !ANIMATABLE,
        (justifyContent, styles) ->
            styles.put(JUSTIFY_CONTENT, JustifyContent.find(justifyContent)),
        JustifyContent::contains);
  }
}
