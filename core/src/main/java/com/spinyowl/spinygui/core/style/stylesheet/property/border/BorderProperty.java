package com.spinyowl.spinygui.core.style.stylesheet.property.border;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_BOTTOM_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_BOTTOM_STYLE;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_BOTTOM_WIDTH;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_LEFT_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_LEFT_STYLE;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_LEFT_WIDTH;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_RIGHT_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_RIGHT_STYLE;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_RIGHT_WIDTH;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_TOP_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_TOP_STYLE;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_TOP_WIDTH;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.stylesheet.property.border.width.BorderWidthProperty;
import com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;
import java.util.HashMap;
import java.util.Map;

public class BorderProperty extends Property {

  public static final String DEFAULT_VALUE = "medium none transparent";

  private static final ValueExtractor<Color> colorValueExtractor = ValueExtractors.of(Color.class);

  public BorderProperty() {
    super(
        BORDER,
        DEFAULT_VALUE,
        !INHERITED,
        ANIMATABLE,
        BorderProperty::x,
        BorderProperty::test,
        true);
  }

  public static boolean test(String value) {
    String[] values = value.split("\\s+");

    if (values.length == 0 || values.length > 3) {
      return false;
    }

    if (values.length == 1) {
      return BorderStyle.contains(values[0]);
    } else if (values.length == 2) {
      if (BorderStyle.contains(values[0])) {
        return BorderWidthProperty.testOne(values[1]) || colorValueExtractor.isValid(values[1]);
      } else if (BorderStyle.contains(values[1])) {
        return BorderWidthProperty.testOne(values[0]) || colorValueExtractor.isValid(values[0]);
      }
      return false;
    } else {
      if (BorderStyle.contains(values[0])) {
        return BorderWidthProperty.testOne(values[1]) && colorValueExtractor.isValid(values[2])
            || BorderWidthProperty.testOne(values[2]) && colorValueExtractor.isValid(values[1]);
      } else if (BorderStyle.contains(values[1])) {
        return BorderWidthProperty.testOne(values[0]) && colorValueExtractor.isValid(values[2])
            || BorderWidthProperty.testOne(values[2]) && colorValueExtractor.isValid(values[0]);
      } else if (BorderStyle.contains(values[2])) {
        return BorderWidthProperty.testOne(values[1]) && colorValueExtractor.isValid(values[0])
            || BorderWidthProperty.testOne(values[0]) && colorValueExtractor.isValid(values[1]);
      }
      return false;
    }
  }

  private static Map<String, Object> x(String value) {
    BorderItem i = extract(value);
    Map<String, Object> border = new HashMap<>();
    if (i.color() != null) {
      border.putAll(
          StyleUtils.getOneFour(
              new Object[] {i.color()},
              BORDER_TOP_COLOR,
              BORDER_RIGHT_COLOR,
              BORDER_BOTTOM_COLOR,
              BORDER_LEFT_COLOR));
    }
    if (i.width() != null) {
      border.putAll(
          StyleUtils.getOneFour(
              new Object[] {i.color()},
              BORDER_TOP_WIDTH,
              BORDER_RIGHT_WIDTH,
              BORDER_BOTTOM_WIDTH,
              BORDER_LEFT_WIDTH));
    }
    if (i.style() != null) {
      border.putAll(
          StyleUtils.getOneFour(
              new Object[] {i.color()},
              BORDER_TOP_STYLE,
              BORDER_RIGHT_STYLE,
              BORDER_BOTTOM_STYLE,
              BORDER_LEFT_STYLE));
    }
    return border;
  }

  public static Map<String, Object> extract(
      String value, String sideStyle, String sideWidth, String sideColor) {
    BorderItem borderItem = BorderProperty.extract(value);
    Map<String, Object> border = new HashMap<>();
    if (borderItem.style() != null) {
      border.put(sideStyle, borderItem.style());
    }
    if (borderItem.width() != null) {
      border.put(sideWidth, borderItem.width());
    }
    if (borderItem.color() != null) {
      border.put(sideColor, borderItem.color());
    }
    return border;
  }

  public static BorderItem extract(String value) {
    var borderItem = new BorderItem();
    String[] values = value.split("\\s+");
    if (values.length == 1) {
      borderItem.style(BorderStyle.find(values[0]));
    } else if (values.length == 2) {
      if (BorderStyle.contains(values[0])) {
        borderItem.style(BorderStyle.find(values[0]));
        if (colorValueExtractor.isValid(values[1])) {
          borderItem.color(colorValueExtractor.extract(values[1]));
        } else {
          borderItem.width(BorderWidthProperty.extractOne(values[1]));
        }
      } else if (BorderStyle.contains(values[1])) {
        borderItem.style(BorderStyle.find(values[1]));
        if (colorValueExtractor.isValid(values[0])) {
          borderItem.color(colorValueExtractor.extract(values[0]));
        } else {
          borderItem.width(BorderWidthProperty.extractOne(values[0]));
        }
      }
    } else if (values.length == 3) {
      if (BorderStyle.contains(values[0])) {
        borderItem.style(BorderStyle.find(values[0]));
        if (colorValueExtractor.isValid(values[1])) {
          borderItem.color(colorValueExtractor.extract(values[1]));
          borderItem.width(BorderWidthProperty.extractOne(values[2]));
        } else {
          borderItem.width(BorderWidthProperty.extractOne(values[1]));
          borderItem.color(colorValueExtractor.extract(values[2]));
        }
      } else if (BorderStyle.contains(values[1])) {
        borderItem.style(BorderStyle.find(values[1]));
        if (colorValueExtractor.isValid(values[0])) {
          borderItem.color(colorValueExtractor.extract(values[0]));
          borderItem.width(BorderWidthProperty.extractOne(values[2]));
        } else {
          borderItem.width(BorderWidthProperty.extractOne(values[0]));
          borderItem.color(colorValueExtractor.extract(values[2]));
        }
      } else if (BorderStyle.contains(values[2])) {
        borderItem.style(BorderStyle.find(values[2]));
        if (colorValueExtractor.isValid(values[0])) {
          borderItem.color(colorValueExtractor.extract(values[0]));
          borderItem.width(BorderWidthProperty.extractOne(values[1]));
        } else {
          borderItem.width(BorderWidthProperty.extractOne(values[0]));
          borderItem.color(colorValueExtractor.extract(values[1]));
        }
      }
    }
    return borderItem;
  }
}
