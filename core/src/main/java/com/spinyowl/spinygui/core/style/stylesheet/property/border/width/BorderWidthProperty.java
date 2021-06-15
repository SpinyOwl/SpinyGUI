package com.spinyowl.spinygui.core.style.stylesheet.property.border.width;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_WIDTH;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.testMultipleValues;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.border.Border;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;

public class BorderWidthProperty extends Property<Border> {

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
        (s, b) -> s.border().width(b),
        NodeStyle::border,
        BorderWidthProperty::extract,
        BorderWidthProperty::test);
  }

  protected static Border extract(String value) {
    var b = new Border();
    String[] v = value.split("\\s+");
    if (v.length == 1) {
      b.width(extractOne(v[0]));
    } else if (v.length == 2) {
      b.width(extractOne(v[0]), extractOne(v[1]));
    } else if (v.length == 3) {
      b.width(extractOne(v[0]), extractOne(v[1]), extractOne(v[2]));
    } else if (v.length >= 4) {
      b.width(extractOne(v[0]), extractOne(v[1]), extractOne(v[2]), extractOne(v[3]));
    }
    return b;
  }

  @SuppressWarnings("rawtypes")
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
