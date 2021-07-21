package com.spinyowl.spinygui.core.style.stylesheet.property.font;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FONT_SIZE;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Length;
import java.util.List;
import java.util.Map;

public class FontSizeProperty extends Property {

  private static final String XX_LARGE = "xx-large";
  private static final String X_LARGE = "x-large";
  private static final String LARGE = "large";
  private static final String MEDIUM = "medium";
  private static final String SMALL = "small";
  private static final String X_SMALL = "x-small";
  private static final String XX_SMALL = "xx-small";

  private static final List<String> values =
      List.of(MEDIUM, XX_SMALL, X_SMALL, SMALL, LARGE, X_LARGE, XX_LARGE);

  private static final ValueExtractor<Length> extractor = ValueExtractors.of(Length.class);

  public FontSizeProperty() {
    super(
        FONT_SIZE,
        MEDIUM,
        INHERITED,
        ANIMATABLE,
        value -> Map.of(FONT_SIZE, extract(value)),
        FontSizeProperty::test);
  }

  public static Length extract(String value) {
    return switch (value) {
      case XX_LARGE -> Length.pixel(22);
      case X_LARGE -> Length.pixel(18);
      case LARGE -> Length.pixel(16);
      case MEDIUM -> Length.pixel(14);
      case SMALL -> Length.pixel(12);
      case X_SMALL -> Length.pixel(10);
      case XX_SMALL -> Length.pixel(9);
      default -> extractor.extract(value);
    };
  }

  public static boolean test(String value) {
    return values.contains(value) || extractor.isValid(value);
  }
}
