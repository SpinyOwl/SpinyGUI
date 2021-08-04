package com.spinyowl.spinygui.core.style.stylesheet.property.background;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.types.background.BackgroundOrigin;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BACKGROUND_ORIGIN;

public class BackgroundOriginProperty extends Property {

  public BackgroundOriginProperty() {
    super(
        BACKGROUND_ORIGIN,
        BackgroundOrigin.PADDING_BOX.name(),
        !INHERITED,
        !ANIMATABLE,
        (value, styles) -> styles.put(BACKGROUND_ORIGIN, BackgroundOrigin.find(value)),
        BackgroundOrigin::contains);
  }
}
