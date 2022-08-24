package com.spinyowl.spinygui.core.style.stylesheet.property.font;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.LINE_HEIGHT;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;

public class LineHeightProperty extends Property {
  private static final ValueExtractor<Float> extractor = ValueExtractors.of(Float.class);
  private static final String DEFAULT_VALUE_KEY = "normal";
  private static final float DEFAULT_VALUE = 1.2F;

  public LineHeightProperty() {
    super(
        LINE_HEIGHT,
        DEFAULT_VALUE_KEY,
        INHERITABLE,
        ANIMATABLE,
        (value, styles) -> styles.put(LINE_HEIGHT, extract(value)),
        LineHeightProperty::test);
  }

  public static Float extract(String value) {
    if (DEFAULT_VALUE_KEY.equals(value)) {
      return DEFAULT_VALUE;
    }
    return extractor.extract(value);
  }

  public static boolean test(String value) {
    return DEFAULT_VALUE_KEY.equals(value) || extractor.isValid(value);
  }
}
