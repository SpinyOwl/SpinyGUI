package com.spinyowl.spinygui.core.style.stylesheet.property.background;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BACKGROUND_ORIGIN;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.types.background.BackgroundOrigin;
import java.util.Map;

public class BackgroundOriginProperty extends Property {

  public BackgroundOriginProperty() {
    super(
        BACKGROUND_ORIGIN,
        BackgroundOrigin.PADDING_BOX.name(),
        !INHERITED,
        !ANIMATABLE,
        value -> Map.of(BACKGROUND_ORIGIN, BackgroundOrigin.find(value)),
        BackgroundOrigin::contains);
  }
}
