package com.spinyowl.spinygui.core.converter.css.property.background;

import static com.spinyowl.spinygui.core.converter.css.Properties.BACKGROUND_POSITION_Y;
import com.spinyowl.spinygui.core.converter.css.model.Property;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Length;
import java.util.List;

public class BackgroundPositionYProperty extends Property<Length> {

  private static final ValueExtractor<Length> extractor = ValueExtractors.of(Length.class);
  private static final String TOP = "top";
  private static final String CENTER = "center";
  private static final String BOTTOM = "bottom";
  private static final List<String> values = List.of(TOP, CENTER, BOTTOM);

  public BackgroundPositionYProperty() {
    super(BACKGROUND_POSITION_Y, "0%", !INHERITED, ANIMATABLE,
        (s, c) -> s.background().position().y(c), s -> s.background().position().y(),
        BackgroundPositionYProperty::extract, BackgroundPositionYProperty::test);
  }

  //@formatter:off
  private static Length extract(String value) {
    switch (value) {
      case TOP: return Length.percent(0);
      case CENTER: return Length.percent(50);
      case BOTTOM: return Length.percent(100);
      default: return extractor.extract(value);
    }
  }
  //@formatter:on

  private static boolean test(String value) {
    return values.contains(value) || extractor.isValid(value);
  }
}
