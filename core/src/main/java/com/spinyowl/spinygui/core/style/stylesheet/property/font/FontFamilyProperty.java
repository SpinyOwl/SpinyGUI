package com.spinyowl.spinygui.core.style.stylesheet.property.font;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.style.stylesheet.Property;

import java.util.Arrays;
import java.util.stream.Collectors;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FONT_FAMILY;

public class FontFamilyProperty extends Property {

  public FontFamilyProperty() {
    super(
        FONT_FAMILY,
        "default",
        INHERITED,
        !ANIMATABLE,
        (v, styles) ->
            styles.put(
                FONT_FAMILY,
                Arrays.stream(v.split(",\\s+"))
                    .map(FontFamilyProperty::trimAndUnwrap)
                    .collect(Collectors.toSet())),
        Font::hasFont);
  }

  private static String trimAndUnwrap(String value) {
    return value.trim().replace("\"", "");
  }
}
