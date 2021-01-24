package com.spinyowl.spinygui.core.style.stylesheet.extractor.impl;

import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractorException;
import com.spinyowl.spinygui.core.node.style.types.Color;

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

  public static final String SEPARATOR = "\\s*?,\\s*?";

  private static final String HEX_STRING_REGEX = "#([0-9a-fA-F]{3}|[0-9a-fA-F]{4}|[0-9a-fA-F]{6}|[0-9a-fA-F]{8})";

  private static final String RGB_FUNCTION_REGEX = "rgb\\x28((2([0-4][0-9]|5[0-5])|[0-1]?[0-9]?[0-9])(\\s*?,\\s*?)){2}(2([0-4][0-9]|5[0-5])|[0-1]?[0-9]?[0-9])\\x29";
  private static final String RGBA_FUNCTION_REGEX = "rgba\\x28(([2]([0-4][0-9]|[5][0-5])|[0-1]?[0-9]?[0-9])(\\s*?,\\s*?)){3}(1|0(.\\d+)?)\\x29";

  private static final String HSL_FUNCTION_REGEX = "hsl\\x28(3([0-5]\\d|60)|[1-2]\\d\\d|\\d{1,2})((\\s*?,\\s*?)(100|\\d{1,2})%){2}\\x29";
  private static final String HSLA_FUNCTION_REGEX = "hsl\\x28(3([0-5]\\d|60)|[1-2]\\d\\d|\\d{1,2})((\\s*?,\\s*?)(100|\\d{1,2})%){2}(\\s*?,\\s*?)(1|0(.\\d+)?)\\x29";

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
            value.matches(HSL_FUNCTION_REGEX) ||
            value.matches(HSLA_FUNCTION_REGEX) ||
            Color.exists(value));
  }

  @Override
  public Color extract(String value) {
    if (value == null) {
      return null;
    }

    Color color = Color.get(value);
    if (color != null) {
      return color;
    }

    if (value.matches(HEX_STRING_REGEX)) {
      return Color.fromHex(value);
    }
    if (value.matches(RGB_FUNCTION_REGEX)) {
      String[] rgb = value.substring(4, value.length() - 1).split(SEPARATOR);
      return new Color(parseInt(rgb[0].trim()), parseInt(rgb[1].trim()), parseInt(rgb[2].trim()));
    }
    if (value.matches(RGBA_FUNCTION_REGEX)) {
      String[] rgba = value.substring(5, value.length() - 1).split(SEPARATOR);
      return new Color(parseInt(rgba[0].trim()), parseInt(rgba[1].trim()),
          parseInt(rgba[2].trim()), parseFloat(rgba[3].trim()));
    }
    if (value.matches(HSL_FUNCTION_REGEX)) {
      String[] hsl = value.substring(4, value.length() - 1).split(SEPARATOR);
      return Color.hslToColor(
          parseInt(hsl[0].trim()), parseInt(hsl[1].trim()),
          parseInt(hsl[2].trim()), 1f);
    }
    if (value.matches(HSLA_FUNCTION_REGEX)) {
      String[] hsla = value.substring(5, value.length() - 1).split(SEPARATOR);
      return Color.hslToColor(
          parseInt(hsla[0].trim()), parseInt(hsla[1].trim()),
          parseInt(hsla[2].trim()), parseFloat(hsla[3].trim()));
    }

    throw new ValueExtractorException(getType(), value);
  }
}
