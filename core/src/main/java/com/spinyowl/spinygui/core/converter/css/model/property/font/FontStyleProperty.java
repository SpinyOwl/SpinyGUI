package com.spinyowl.spinygui.core.converter.css.model.property.font;

import static com.spinyowl.spinygui.core.converter.css.Properties.FONT_STYLE;
import com.spinyowl.spinygui.core.converter.css.model.Property;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.style.NodeStyle;

public class FontStyleProperty extends Property<FontStyle> {

  public FontStyleProperty() {
    super(FONT_STYLE, "normal", INHERITED, !ANIMATABLE, NodeStyle::fontStyle,
        NodeStyle::fontStyle, FontStyle::find, FontStyle::contains);
  }

}
