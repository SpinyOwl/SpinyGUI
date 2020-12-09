package com.spinyowl.spinygui.core.converter.css.model.property.font;

import static com.spinyowl.spinygui.core.converter.css.Properties.FONT_SIZE;
import com.spinyowl.spinygui.core.converter.css.model.Property;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.length.Length;
import java.util.List;

public class FontSizeProperty extends Property<Length> {

  private static final String XX_LARGE = "xx-large";
  private static final String X_LARGE = "x-large";
  private static final String LARGE = "large";
  private static final String MEDIUM = "medium";
  private static final String SMALL = "small";
  private static final String X_SMALL = "x-small";
  private static final String XX_SMALL = "xx-small";

  private static final List<String> values = List
      .of(MEDIUM, XX_SMALL, X_SMALL, SMALL, LARGE, X_LARGE, XX_LARGE);

  private static final ValueExtractor<Length> extractor = ValueExtractors.of(Length.class);

  public FontSizeProperty() {
    super(FONT_SIZE, MEDIUM, INHERITED, ANIMATABLE, NodeStyle::fontSize,
        NodeStyle::fontSize, FontSizeProperty::extract, FontSizeProperty::test);
  }

  public static Length extract(String value) {
    switch (value) {
      case XX_LARGE:
        return Length.pixel(22);
      case X_LARGE:
        return Length.pixel(18);
      case LARGE:
        return Length.pixel(16);
      case MEDIUM:
        return Length.pixel(14);
      case SMALL:
        return Length.pixel(12);
      case X_SMALL:
        return Length.pixel(10);
      case XX_SMALL:
        return Length.pixel(9);
      default:
        return extractor.extract(value);
    }
  }

  public static boolean test(String value) {
    return values.contains(value) || extractor.isValid(value);
  }

}
