package com.spinyowl.spinygui.core.style.stylesheet.property.border;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.stylesheet.property.border.width.BorderWidthProperty;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.border.Border;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;

public class BorderProperty extends Property<Border> {

  public static final String DEFAULT_VALUE = "medium none transparent";

  private static final ValueExtractor<Color> colorValueExtractor = ValueExtractors.of(Color.class);

  public BorderProperty() {
    super(
        BORDER,
        DEFAULT_VALUE,
        !INHERITED,
        ANIMATABLE,
        NodeStyle::border,
        NodeStyle::border,
        BorderProperty::x,
        BorderProperty::test);
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

  private static Border x(String value) {
    var border = new Border();
    BorderItem i = extract(value);
    border.style(i.style());
    border.color(i.color());
    border.width(i.width());
    return border;
  }

  public static BorderItem extract(String value) {
    var i = new BorderItem();
    String[] values = value.split("\\s+");
    if (values.length == 1) {
      i.style(BorderStyle.find(values[0]));
    } else if (values.length == 2) {
      if (BorderStyle.contains(values[0])) {
        i.style(BorderStyle.find(values[0]));
        if (colorValueExtractor.isValid(values[1])) {
          i.color(colorValueExtractor.extract(values[1]));
        } else {
          i.width(BorderWidthProperty.extractOne(values[1]));
        }
      } else if (BorderStyle.contains(values[1])) {
        i.style(BorderStyle.find(values[1]));
        if (colorValueExtractor.isValid(values[0])) {
          i.color(colorValueExtractor.extract(values[0]));
        } else {
          i.width(BorderWidthProperty.extractOne(values[0]));
        }
      }
    } else if (values.length == 3) {
      if (BorderStyle.contains(values[0])) {
        i.style(BorderStyle.find(values[0]));
        if (colorValueExtractor.isValid(values[1])) {
          i.color(colorValueExtractor.extract(values[1]));
          i.width(BorderWidthProperty.extractOne(values[2]));
        } else {
          i.width(BorderWidthProperty.extractOne(values[1]));
          i.color(colorValueExtractor.extract(values[2]));
        }
      } else if (BorderStyle.contains(values[1])) {
        i.style(BorderStyle.find(values[1]));
        if (colorValueExtractor.isValid(values[0])) {
          i.color(colorValueExtractor.extract(values[0]));
          i.width(BorderWidthProperty.extractOne(values[2]));
        } else {
          i.width(BorderWidthProperty.extractOne(values[0]));
          i.color(colorValueExtractor.extract(values[2]));
        }
      } else if (BorderStyle.contains(values[2])) {
        i.style(BorderStyle.find(values[2]));
        if (colorValueExtractor.isValid(values[0])) {
          i.color(colorValueExtractor.extract(values[0]));
          i.width(BorderWidthProperty.extractOne(values[1]));
        } else {
          i.width(BorderWidthProperty.extractOne(values[0]));
          i.color(colorValueExtractor.extract(values[1]));
        }
      }
    }
    return i;
  }
}
