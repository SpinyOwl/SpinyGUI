package com.spinyowl.spinygui.core.style.types;

import static java.lang.String.format;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;

@With
@Value
@AllArgsConstructor
public class Color {

  // @formatter:off
  public static final Color BLACK = new Color(0, 0, 0);
  public static final Color SILVER = new Color(192, 192, 192);
  public static final Color GRAY = new Color(128, 128, 128);
  public static final Color WHITE = new Color(255, 255, 255);
  public static final Color MAROON = new Color(128, 0, 0);
  public static final Color RED = new Color(255, 0, 0);
  public static final Color PURPLE = new Color(128, 0, 128);
  public static final Color FUCHSIA = new Color(255, 0, 255);
  public static final Color GREEN = new Color(0, 128, 0);
  public static final Color LIME = new Color(0, 255, 0);
  public static final Color OLIVE = new Color(128, 128, 0);
  public static final Color YELLOW = new Color(255, 255, 0);
  public static final Color NAVY = new Color(0, 0, 128);
  public static final Color BLUE = new Color(0, 0, 255);
  public static final Color TEAL = new Color(0, 128, 128);
  public static final Color AQUA = new Color(0, 255, 255);
  public static final Color TRANSPARENT = new Color(0f, 0f, 0f, 0f);
  // @formatter:on

  private static final String COMMA = ",";
  private static final String OUTSIDE_OF_EXPECTED_RANGE_S =
      "Color parameter outside of expected range - %s";

  private static Map<String, Color> colors = new HashMap<>();

  static {
    colors.put("black", BLACK);
    colors.put("silver", SILVER);
    colors.put("gray", GRAY);
    colors.put("white", WHITE);
    colors.put("maroon", MAROON);
    colors.put("red", RED);
    colors.put("purple", PURPLE);
    colors.put("fuchsia", FUCHSIA);
    colors.put("green", GREEN);
    colors.put("lime", LIME);
    colors.put("olive", OLIVE);
    colors.put("yellow", YELLOW);
    colors.put("navy", NAVY);
    colors.put("blue", BLUE);
    colors.put("teal", TEAL);
    colors.put("aqua", AQUA);
    colors.put("transparent", TRANSPARENT);
  }

  float r;
  float g;
  float b;
  float a;

  public Color(int r, int g, int b, int a) {
    this(r, g, b, a / 255f);
  }

  public Color(int r, int g, int b, float a) {
    this(r / 255f, g / 255f, b / 255f, a);
  }

  public Color(float r, float g, float b) {
    this(r, g, b, 1f);
  }

  public Color(int r, int g, int b) {
    this(r / 255f, g / 255f, b / 255f, 1f);
  }

  public static Color get(String name) {
    return colors.get(name.toLowerCase());
  }

  /**
   * Allowed to parse hex strings in RGB/RGBA/RRGGBB/RRGGBBAA format.
   * <p>
   * Example
   *
   * @param value hex value
   * @return color
   */
  public static Color fromHex(String value) {
    if (value.startsWith("#")) {
      value = value.substring(1);
    }
    value = value.trim();
    var hex = Integer.parseInt(value, 16);
    return switch (value.length()) {
      case 8 -> new Color((hex >> 24) & 0xFF, (hex >> 16) & 0xFF, (hex >> 8) & 0xFF, hex & 0xFF);
      case 6 -> new Color((hex >> 16) & 0xFF, (hex >> 8) & 0xFF, hex & 0xFF);
      case 4 -> new Color((hex >> 12) & 0xF, (hex >> 8) & 0xF, (hex >> 4) & 0xF, hex & 0xF);
      case 3 -> new Color((hex >> 8) & 0xF, (hex >> 4) & 0xF, hex & 0xF);
      default -> null;
    };
  }

  /**
   * Convert HSL values to a RGB Color.
   *
   * @param hue        Hue is specified as degrees in the range 0 - 360.
   * @param saturation Saturation is specified as a percentage in the range 1 - 100.
   * @param lightness  lightness is specified as a percentage in the range 1 - 100.
   * @param alpha      the alpha value between 0 - 1
   * @return the Color object.
   */
  public static Color hslToColor(int hue, int saturation, int lightness, float alpha) {
    if (saturation < 0 || saturation > 100) {
      throw new IllegalArgumentException(format(OUTSIDE_OF_EXPECTED_RANGE_S, "Saturation"));
    }

    if (lightness < 0 || lightness > 100) {
      throw new IllegalArgumentException(format(OUTSIDE_OF_EXPECTED_RANGE_S, "Lightness"));
    }

    if (alpha < 0.0f || alpha > 1.0f) {
      throw new IllegalArgumentException(format(OUTSIDE_OF_EXPECTED_RANGE_S, "Alpha"));
    }

    // Formula needs all values between 0 - 1.
    float h = (hue % 360.0f) / 360f;
    float s = saturation / 100f;
    float l = lightness / 100f;

    float q;

    if (l < 0.5) {
      q = l * (1 + s);
    } else {
      q = (l + s) - (s * l);
    }

    float p = 2 * l - q;

    float r = Math.max(0, hueToRGB(p, q, h + (1.0f / 3.0f)));
    float g = Math.max(0, hueToRGB(p, q, h));
    float b = Math.max(0, hueToRGB(p, q, h - (1.0f / 3.0f)));

    r = Math.min(r, 1.0f);
    g = Math.min(g, 1.0f);
    b = Math.min(b, 1.0f);

    return new Color(r, g, b, alpha);
  }

  // @formatter:off
  private static float hueToRGB(float p, float q, float h) {
    if (h < 0) h += 1;
    if (h > 1) h -= 1;
    if (6 * h < 1) return p + ((q - p) * 6 * h);
    if (2 * h < 1) return q;
    if (3 * h < 2) return p + ((q - p) * 6 * ((2.0f / 3.0f) - h));
    return p;
  }
  // @formatter:on

  public static boolean exists(String colorName) {
    return colors.containsKey(colorName.toLowerCase());
  }

  @Override
  public String toString() {
    return "rgba(" + r + ", " + g + ", " + b + ", " + a + ")";
  }
}
