package com.spinyowl.spinygui.core.converter.css.extractor.impl;

import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractorException;
import com.spinyowl.spinygui.core.style.types.Color;

/**
 * Extractor which extracts {@link Color} from string.
 * <p>
 * Currently supported:
 * <ul>
 *     <li>3/4/6/8 hex values: #RGB, #RGBA, #RRGGBB, #RRGGBBAA</li>
 *     <li>rgb and rgba functions: rgb(r, g, b), rgba(r, g, b, a)</li>
 * </ul>
 */
public class ColorValueExtractor implements ValueExtractor<Color> {

  private static final String HEX_STRING_REGEX = "#([0-9a-fA-F]{3}|[0-9a-fA-F]{4}|[0-9a-fA-F]{6}|[0-9a-fA-F]{8})";

  private static final String RGB_FUNCTION_REGEX = "rgb\\x28((2([0-4][0-9]|5[0-5])|[0-1]?[0-9]?[0-9])(\\s*?,\\s*?)){2}(2([0-4][0-9]|5[0-5])|[0-1]?[0-9]?[0-9])\\x29";
  private static final String RGBA_FUNCTION_REGEX = "rgba\\x28(([2]([0-4][0-9]|[5][0-5])|[0-1]?[0-9]?[0-9])(\\s*?,\\s*?)){3}(1|0(.\\d+)?)\\x29";

//    private static final String HSL_FUNCTION_REGEX = "hsl\\x28(3([0-5]\\d|60)|[1-2]\\d\\d|\\d{1,2})((\\s*?,\\s*?)(100|\\d{1,2})%){2}\\x29";
//    private static final String HSLA_FUNCTION_REGEX = "hsl\\x28(3([0-5]\\d|60)|[1-2]\\d\\d|\\d{1,2})((\\s*?,\\s*?)(100|\\d{1,2})%){2}(\\s*?,\\s*?)(1|0(.\\d+)?)\\x29";

  @Override
  public Class<Color> getType() {
    return Color.class;
  }

  @Override
  public boolean isValid(String value) {
    return value != null &&
        (value.matches(HEX_STRING_REGEX) ||
            value.matches(RGB_FUNCTION_REGEX) ||
            value.matches(RGBA_FUNCTION_REGEX) ||
//              value.matches(HSL_FUNCTION_REGEX) ||
//              value.matches(HSLA_FUNCTION_REGEX) ||
            Color.exists(value));
  }

  @Override
  public Color extract(String value) {
    if (value == null) {
      return null;
    }

    Color color = Color.getColorByName(value);
    if (color != null) {
      return color;
    }

    if (value.matches(HEX_STRING_REGEX)) {
      return Color.parseHexString(value);
    }
    if (value.matches(RGB_FUNCTION_REGEX)) {
      return Color.parseRGBAColorString(value.substring(4, value.length() - 1));
    }

    throw new ValueExtractorException(getType(), value);
  }
}
