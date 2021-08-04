package com.spinyowl.spinygui.core.style.stylesheet.property.background;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.background.BackgroundSize;
import com.spinyowl.spinygui.core.style.types.length.Unit;

import java.util.List;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BACKGROUND_SIZE;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.testMultipleValues;

public class BackgroundSizeProperty extends Property {

  private static final ValueExtractor<Unit> extractor = ValueExtractors.of(Unit.class);
  private static final String COVER = "cover";
  private static final String CONTAIN = "contain";
  private static final List<String> values = List.of(COVER, CONTAIN);

  public BackgroundSizeProperty() {
    super(
        BACKGROUND_SIZE,
        "auto",
        !INHERITED,
        ANIMATABLE,
        (value, styles) -> styles.put(BACKGROUND_SIZE, extract(value)),
        BackgroundSizeProperty::test);
  }

  private static BackgroundSize extract(String value) {
    switch (value) {
      case COVER:
        return BackgroundSize.createCover();
      case CONTAIN:
        return BackgroundSize.createContain();
      default:
        String[] v = value.split("\\s+");
        if (v.length == 1) {
          return BackgroundSize.createSize(extractor.extract(v[0]));
        } else {
          return BackgroundSize.createSize(extractor.extract(v[0]), extractor.extract(v[1]));
        }
    }
  }

  private static boolean test(String value) {
    return values.contains(value) || testMultipleValues(value, "\\s+", 1, 2, extractor::isValid);
  }
}
