package com.spinyowl.spinygui.core.style.stylesheet.property.background;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BACKGROUND_POSITION;
import com.spinyowl.spinygui.core.node.style.types.background.BackgroundPosition;
import com.spinyowl.spinygui.core.node.style.types.length.Length;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import java.util.List;

public class BackgroundPositionProperty extends Property<BackgroundPosition> {

  private static final ValueExtractor<Length> extractor = ValueExtractors.of(Length.class);
  private static final String SPLITERATOR = "\\s+";
  private static final String LEFT = "left";
  private static final String CENTER = "center";
  private static final String RIGHT = "right";
  private static final String TOP = "top";
  private static final String BOTTOM = "bottom";
  private static final List<String> X_VALUES = List.of(LEFT, CENTER, RIGHT);
  private static final List<String> Y_VALUES = List.of(TOP, CENTER, BOTTOM);

  public BackgroundPositionProperty() {
    super(
        BACKGROUND_POSITION,
        "0% 0%",
        !INHERITED,
        ANIMATABLE,
        (s, c) -> s.background().position(c),
        s -> s.background().position(),
        BackgroundPositionProperty::extract,
        BackgroundPositionProperty::test);
  }

  private static BackgroundPosition extract(String value) {
    String[] values = value.split(SPLITERATOR);
    if (values.length == 1) {
      return new BackgroundPosition(extract(values[0], X_VALUES));
    } else {
      return new BackgroundPosition(extract(values[0], X_VALUES), extract(values[1], Y_VALUES));
    }
  }

  private static Length extract(String value, List<String> values) {
    if (values.contains(value)) {
      return Length.percent(values.indexOf(value) * 100f / (values.size() - 1));
    } else {
      return extractor.extract(value);
    }
  }

  private static boolean test(String value) {
    String[] values = value.split(SPLITERATOR);
    if (values.length <= 0 || values.length > 2) {
      return false;
    } else if (values.length == 1) {
      return X_VALUES.contains(values[0]) || extractor.isValid(values[0]);
    } else {
      return (X_VALUES.contains(values[0]) || extractor.isValid(values[0]))
          && (Y_VALUES.contains(values[1]) || extractor.isValid(values[1]));
    }
  }
}
