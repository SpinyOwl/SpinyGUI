package com.spinyowl.spinygui.core.style.stylesheet.property.margin;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.MARGIN;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.MARGIN_BOTTOM;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.MARGIN_LEFT;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.MARGIN_RIGHT;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.MARGIN_TOP;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.setOneFour;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.testMultipleValues;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Unit;
import java.util.Arrays;
import java.util.Map;

public class MarginProperty extends Property {

  private static final ValueExtractor<Unit> unitValueExtractor = ValueExtractors.of(Unit.class);

  public MarginProperty() {
    super(MARGIN, "0", !INHERITABLE, ANIMATABLE, MarginProperty::extract, MarginProperty::test, true);
  }

  private static void extract(String value, Map<String, Object> styles) {
    setOneFour(
        Arrays.stream(value.trim().split("\\s+")).map(unitValueExtractor::extract).toArray(),
        MARGIN_TOP,
        MARGIN_RIGHT,
        MARGIN_BOTTOM,
        MARGIN_LEFT,
        styles);
  }

  public static boolean test(String value) {
    return testMultipleValues(value, "\\s+", 1, 4, unitValueExtractor::isValid);
  }
}
