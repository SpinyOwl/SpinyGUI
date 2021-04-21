package com.spinyowl.spinygui.core.style.stylesheet.property.background;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BACKGROUND_ORIGIN;
import com.spinyowl.spinygui.core.node.style.types.background.BackgroundOrigin;
import com.spinyowl.spinygui.core.style.stylesheet.Property;

public class BackgroundOriginProperty extends Property<BackgroundOrigin> {

  public BackgroundOriginProperty() {
    super(
        BACKGROUND_ORIGIN,
        BackgroundOrigin.PADDING_BOX.name(),
        !INHERITED,
        !ANIMATABLE,
        (n, o) -> n.background().origin(o),
        n -> n.background().origin(),
        BackgroundOrigin::find,
        BackgroundOrigin::contains);
  }
}
