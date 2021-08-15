package com.spinyowl.spinygui.core.style.stylesheet.property.padding;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.PADDING;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.PADDING_BOTTOM;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.PADDING_LEFT;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.PADDING_RIGHT;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.PADDING_TOP;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.setOneFour;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.testMultipleValues;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Length;
import java.util.Arrays;
import java.util.Map;

public class PaddingProperty extends Property {

  private static final ValueExtractor<Length> lengthValueExtractor =
      ValueExtractors.of(Length.class);

  public PaddingProperty() {
    super(
        PADDING,
        "0",
        !INHERITED,
        ANIMATABLE,
        PaddingProperty::extract,
        PaddingProperty::test,
        true);
  }

  public static void extract(String value, Map<String, Object> styles) {
    setOneFour(
        Arrays.stream(value.split("\\s+")).map(lengthValueExtractor::extract).toArray(),
        PADDING_TOP,
        PADDING_RIGHT,
        PADDING_BOTTOM,
        PADDING_LEFT,
        styles);
  }

  public static boolean test(String value) {
    return testMultipleValues(value, "\\s+", 1, 4, lengthValueExtractor::isValid);
  }
}
