package com.spinyowl.spinygui.core.style.stylesheet.property.border.width;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_BOTTOM_WIDTH;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_LEFT_WIDTH;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_RIGHT_WIDTH;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_TOP_WIDTH;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_WIDTH;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.testMultipleValues;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.style.types.length.Length.PixelLength;
import java.util.Arrays;
import java.util.Map;

public class BorderWidthProperty extends Property {

  public static final String THIN = "thin";
  public static final String MEDIUM = "medium";
  public static final String THICK = "thick";

  public static final PixelLength THIN_VALUE = Length.pixel(2);
  public static final PixelLength MEDIUM_VALUE = Length.pixel(4);
  public static final PixelLength THICK_VALUE = Length.pixel(6);

  private static final ValueExtractor<PixelLength> extractor = ValueExtractors.of(PixelLength.class);

  public BorderWidthProperty() {
    super(
        BORDER_WIDTH,
        MEDIUM,
        !INHERITABLE,
        ANIMATABLE,
        BorderWidthProperty::extract,
        BorderWidthProperty::test, true);
  }

  protected static void extract(String value, Map<String, Object> styles) {
    StyleUtils.setOneFour(Arrays.stream(value.split("\\s+")).map(BorderWidthProperty::extractOne).toArray(),
        BORDER_TOP_WIDTH,
            BORDER_RIGHT_WIDTH,
            BORDER_BOTTOM_WIDTH,
            BORDER_LEFT_WIDTH,
            styles);
  }

  public static PixelLength extractOne(String value) {
    String lowerCaseValue = value.toLowerCase();
    return switch (lowerCaseValue) {
      case THIN -> THIN_VALUE;
      case MEDIUM -> MEDIUM_VALUE;
      case THICK -> THICK_VALUE;
      default -> extractor.extract(lowerCaseValue);
    };
  }

  public static boolean testOne(String borderWidth) {
    return THIN.equalsIgnoreCase(borderWidth)
        || MEDIUM.equalsIgnoreCase(borderWidth)
        || THICK.equalsIgnoreCase(borderWidth)
        || extractor.isValid(borderWidth);
  }

  private static boolean test(String value) {
    return testMultipleValues(value, "\\s+", 1, 4, BorderWidthProperty::testOne);
  }
}
