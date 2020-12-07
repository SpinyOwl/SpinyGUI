package com.spinyowl.spinygui.core.converter.css.property.background;

import static com.spinyowl.spinygui.core.converter.css.Properties.BACKGROUND_ORIGIN;
import com.spinyowl.spinygui.core.converter.css.model.Property;
import com.spinyowl.spinygui.core.style.types.background.BackgroundOrigin;

public class BackgroundOriginProperty extends Property<BackgroundOrigin> {

  public BackgroundOriginProperty() {
    super(BACKGROUND_ORIGIN, BackgroundOrigin.PADDING_BOX.name(), !INHERITED, !ANIMATABLE,
        (n, o) -> n.background().origin(o), n -> n.background().origin(),
        BackgroundOrigin::find, BackgroundOrigin::contains);
  }
}
