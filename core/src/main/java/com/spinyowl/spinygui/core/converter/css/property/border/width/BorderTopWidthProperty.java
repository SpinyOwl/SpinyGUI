package com.spinyowl.spinygui.core.converter.css.property.border.width;

import static com.spinyowl.spinygui.core.converter.css.Properties.BORDER_TOP_WIDTH;
import static com.spinyowl.spinygui.core.converter.css.property.border.width.BorderWidthProperty.MEDIUM;

import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class BorderTopWidthProperty extends Property<Length> {

  public BorderTopWidthProperty() {
    super(BORDER_TOP_WIDTH, MEDIUM, !INHERITED, ANIMATABLE,
      (s, v) -> s.border().top().width(v),
      s -> s.border().top().width(),
      BorderWidthProperty::extractOne, BorderWidthProperty::testOne);
  }
}
