package com.spinyowl.spinygui.core.style.stylesheet.property.background;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BACKGROUND_POSITION_Y;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import java.util.List;

public class BackgroundPositionYProperty extends Property<Length> {

  private static final ValueExtractor<Length> extractor = ValueExtractors.of(Length.class);
  private static final String TOP = "top";
  private static final String CENTER = "center";
  private static final String BOTTOM = "bottom";
  private static final List<String> values = List.of(TOP, CENTER, BOTTOM);

  public BackgroundPositionYProperty() {
    super(
        BACKGROUND_POSITION_Y,
        "0%",
        !INHERITED,
        ANIMATABLE,
        NodeStyle::backgroundPositionY,
        NodeStyle::backgroundPositionY,
        BackgroundPositionYProperty::extract,
        BackgroundPositionYProperty::test);
  }

  // @formatter:off
  private static Length<?> extract(String value) {
    return switch (value) {
      case TOP -> Length.percent(0);
      case CENTER -> Length.percent(50);
      case BOTTOM -> Length.percent(100);
      default -> extractor.extract(value);
    };
  }
  // @formatter:on

  private static boolean test(String value) {
    return values.contains(value) || extractor.isValid(value);
  }
}
