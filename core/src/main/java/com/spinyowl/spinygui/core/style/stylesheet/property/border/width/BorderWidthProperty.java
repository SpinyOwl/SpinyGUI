package com.spinyowl.spinygui.core.style.stylesheet.property.border.width;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_WIDTH;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.testMultipleValues;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Length;
import java.util.Arrays;

@SuppressWarnings("rawtypes")
public class BorderWidthProperty extends Property<Length[]> {

  public static final String THIN = "thin";
  public static final String MEDIUM = "medium";
  public static final String THICK = "thick";

  public static final Length<Integer> THIN_VALUE = Length.pixel(2);
  public static final Length<Integer> MEDIUM_VALUE = Length.pixel(4);
  public static final Length<Integer> THICK_VALUE = Length.pixel(6);

  @SuppressWarnings("rawtypes")
  private static final ValueExtractor<Length> extractor = ValueExtractors.of(Length.class);

  public BorderWidthProperty() {
    super(
        BORDER_WIDTH,
        MEDIUM,
        !INHERITED,
        ANIMATABLE,
        NodeStyle::borderWidth,
        NodeStyle::borderWidth,
        BorderWidthProperty::extract,
        BorderWidthProperty::test);
  }

  protected static Length[] extract(String value) {
    return Arrays.stream(value.split("\\s+")).map(BorderWidthProperty::extractOne).toArray(Length[]::new);
  }

  public static Length extractOne(String value) {
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
