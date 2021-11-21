package com.spinyowl.spinygui.core.style.stylesheet.property.font;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FONT_SIZE;
import com.spinyowl.spinygui.core.font.FontSize;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class FontSizeProperty extends Property {
  @SuppressWarnings("rawtypes")
  private static final ValueExtractor<Length> extractor = ValueExtractors.of(Length.class);

  public FontSizeProperty() {
    super(
        FONT_SIZE,
        FontSize.MEDIUM.name(),
        INHERITED,
        ANIMATABLE,
        (value, styles) -> styles.put(FONT_SIZE, extract(value)),
        FontSizeProperty::test);
  }

  @SuppressWarnings("rawtypes")
  public static Length extract(String value) {
    if (FontSize.contains(value)) {
      return Length.pixel(FontSize.find(value).size());
    }
    return extractor.extract(value);
  }

  public static boolean test(String value) {
    return FontSize.contains(value) || extractor.isValid(value);
  }
}
