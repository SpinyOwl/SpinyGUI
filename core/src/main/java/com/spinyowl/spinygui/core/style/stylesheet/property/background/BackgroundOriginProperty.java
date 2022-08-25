package com.spinyowl.spinygui.core.style.stylesheet.property.background;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BACKGROUND_ORIGIN;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.types.background.BackgroundOrigin;

public class BackgroundOriginProperty extends Property {

  public BackgroundOriginProperty() {
    super(
        BACKGROUND_ORIGIN,
        new TermIdent(BackgroundOrigin.PADDING_BOX.name()),
        !INHERITABLE,
        !ANIMATABLE,
        put(BACKGROUND_ORIGIN, TermIdent.class, BackgroundOrigin::find),
        check(TermIdent.class, BackgroundOrigin::contains));
  }
}
