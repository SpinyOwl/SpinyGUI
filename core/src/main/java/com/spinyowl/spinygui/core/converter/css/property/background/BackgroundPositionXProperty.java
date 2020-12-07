package com.spinyowl.spinygui.core.converter.css.property.background;

import static com.spinyowl.spinygui.core.converter.css.Properties.BACKGROUND_POSITION_X;
import com.spinyowl.spinygui.core.converter.css.model.Property;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Length;
import java.util.List;

public class BackgroundPositionXProperty extends Property<Length> {

  private static final ValueExtractor<Length> extractor = ValueExtractors.of(Length.class);
  private static final String LEFT = "left";
  private static final String CENTER = "center";
  private static final String RIGHT = "right";
  private static final List<String> values = List.of(LEFT, CENTER, RIGHT);

  public BackgroundPositionXProperty() {
    super(BACKGROUND_POSITION_X, "0%", !INHERITED, ANIMATABLE,
        (s, c) -> s.background().position().x(c), s -> s.background().position().x(),
        BackgroundPositionXProperty::extract, BackgroundPositionXProperty::test);
  }

  //@formatter:off
  private static Length extract(String value) {
    switch (value) {
      case LEFT: return Length.percent(0);
      case CENTER: return Length.percent(50);
      case RIGHT: return Length.percent(100);
      default: return extractor.extract(value);
    }
  }
  //@formatter:on

  private static boolean test(String value) {
    return values.contains(value) || extractor.isValid(value);
  }
}
