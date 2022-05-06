package com.spinyowl.spinygui.core.style.types;

import static java.lang.String.format;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

@With
@Value
@AllArgsConstructor
public class Color {

  private static final String COMMA = ",";
  private static final String OUTSIDE_OF_EXPECTED_RANGE_S =
      "Color parameter outside of expected range - %s";

  private static Map<String, Color> colors = new HashMap<>();

  public static final Color TRANSPARENT = namedColor("transparent", new Color(0f, 0f, 0f, 0f));
  public static final Color ALICEBLUE = namedColor("aliceblue", fromHex("#f0f8ff"));
  public static final Color ANTIQUEWHITE = namedColor("antiquewhite", fromHex("#faebd7"));
  public static final Color AQUA = namedColor("aqua", fromHex("#00ffff"));
  public static final Color AQUAMARINE = namedColor("aquamarine", fromHex("#7fffd4"));
  public static final Color AZURE = namedColor("azure", fromHex("#f0ffff"));
  public static final Color BEIGE = namedColor("beige", fromHex("#f5f5dc"));
  public static final Color BISQUE = namedColor("bisque", fromHex("#ffe4c4"));
  public static final Color BLACK = namedColor("black", fromHex("#000000"));
  public static final Color BLANCHEDALMOND = namedColor("blanchedalmond", fromHex("#ffebcd"));
  public static final Color BLUE = namedColor("blue", fromHex("#0000ff"));
  public static final Color BLUEVIOLET = namedColor("blueviolet", fromHex("#8a2be2"));
  public static final Color BROWN = namedColor("brown", fromHex("#a52a2a"));
  public static final Color BURLYWOOD = namedColor("burlywood", fromHex("#deb887"));
  public static final Color CADETBLUE = namedColor("cadetblue", fromHex("#5f9ea0"));
  public static final Color CHARTREUSE = namedColor("chartreuse", fromHex("#7fff00"));
  public static final Color CHOCOLATE = namedColor("chocolate", fromHex("#d2691e"));
  public static final Color CORAL = namedColor("coral", fromHex("#ff7f50"));
  public static final Color CORNFLOWERBLUE = namedColor("cornflowerblue", fromHex("#6495ed"));
  public static final Color CORNSILK = namedColor("cornsilk", fromHex("#fff8dc"));
  public static final Color CRIMSON = namedColor("crimson", fromHex("#dc143c"));
  public static final Color CYAN = namedColor("cyan", fromHex("#00ffff"));
  public static final Color DARKBLUE = namedColor("darkblue", fromHex("#00008b"));
  public static final Color DARKCYAN = namedColor("darkcyan", fromHex("#008b8b"));
  public static final Color DARKGOLDENROD = namedColor("darkgoldenrod", fromHex("#b8860b"));
  public static final Color DARKGRAY = namedColor("darkgray", fromHex("#a9a9a9"));
  public static final Color DARKGREEN = namedColor("darkgreen", fromHex("#006400"));
  public static final Color DARKGREY = namedColor("darkgrey", fromHex("#a9a9a9"));
  public static final Color DARKKHAKI = namedColor("darkkhaki", fromHex("#bdb76b"));
  public static final Color DARKMAGENTA = namedColor("darkmagenta", fromHex("#8b008b"));
  public static final Color DARKOLIVEGREEN = namedColor("darkolivegreen", fromHex("#556b2f"));
  public static final Color DARKORANGE = namedColor("darkorange", fromHex("#ff8c00"));
  public static final Color DARKORCHID = namedColor("darkorchid", fromHex("#9932cc"));
  public static final Color DARKRED = namedColor("darkred", fromHex("#8b0000"));
  public static final Color DARKSALMON = namedColor("darksalmon", fromHex("#e9967a"));
  public static final Color DARKSEAGREEN = namedColor("darkseagreen", fromHex("#8fbc8f"));
  public static final Color DARKSLATEBLUE = namedColor("darkslateblue", fromHex("#483d8b"));
  public static final Color DARKSLATEGRAY = namedColor("darkslategray", fromHex("#2f4f4f"));
  public static final Color DARKSLATEGREY = namedColor("darkslategrey", fromHex("#2f4f4f"));
  public static final Color DARKTURQUOISE = namedColor("darkturquoise", fromHex("#00ced1"));
  public static final Color DARKVIOLET = namedColor("darkviolet", fromHex("#9400d3"));
  public static final Color DEEPPINK = namedColor("deeppink", fromHex("#ff1493"));
  public static final Color DEEPSKYBLUE = namedColor("deepskyblue", fromHex("#00bfff"));
  public static final Color DIMGRAY = namedColor("dimgray", fromHex("#696969"));
  public static final Color DIMGREY = namedColor("dimgrey", fromHex("#696969"));
  public static final Color DODGERBLUE = namedColor("dodgerblue", fromHex("#1e90ff"));
  public static final Color FIREBRICK = namedColor("firebrick", fromHex("#b22222"));
  public static final Color FLORALWHITE = namedColor("floralwhite", fromHex("#fffaf0"));
  public static final Color FORESTGREEN = namedColor("forestgreen", fromHex("#228b22"));
  public static final Color FUCHSIA = namedColor("fuchsia", fromHex("#ff00ff"));
  public static final Color GAINSBORO = namedColor("gainsboro", fromHex("#dcdcdc"));
  public static final Color GHOSTWHITE = namedColor("ghostwhite", fromHex("#f8f8ff"));
  public static final Color GOLD = namedColor("gold", fromHex("#ffd700"));
  public static final Color GOLDENROD = namedColor("goldenrod", fromHex("#daa520"));
  public static final Color GRAY = namedColor("gray", fromHex("#808080"));
  public static final Color GREEN = namedColor("green", fromHex("#008000"));
  public static final Color GREENYELLOW = namedColor("greenyellow", fromHex("#adff2f"));
  public static final Color GREY = namedColor("grey", fromHex("#808080"));
  public static final Color HONEYDEW = namedColor("honeydew", fromHex("#f0fff0"));
  public static final Color HOTPINK = namedColor("hotpink", fromHex("#ff69b4"));
  public static final Color INDIANRED = namedColor("indianred", fromHex("#cd5c5c"));
  public static final Color INDIGO = namedColor("indigo", fromHex("#4b0082"));
  public static final Color IVORY = namedColor("ivory", fromHex("#fffff0"));
  public static final Color KHAKI = namedColor("khaki", fromHex("#f0e68c"));
  public static final Color LAVENDER = namedColor("lavender", fromHex("#e6e6fa"));
  public static final Color LAVENDERBLUSH = namedColor("lavenderblush", fromHex("#fff0f5"));
  public static final Color LAWNGREEN = namedColor("lawngreen", fromHex("#7cfc00"));
  public static final Color LEMONCHIFFON = namedColor("lemonchiffon", fromHex("#fffacd"));
  public static final Color LIGHTBLUE = namedColor("lightblue", fromHex("#add8e6"));
  public static final Color LIGHTCORAL = namedColor("lightcoral", fromHex("#f08080"));
  public static final Color LIGHTCYAN = namedColor("lightcyan", fromHex("#e0ffff"));
  public static final Color LIGHTGOLDENRODYELLOW =
      namedColor("lightgoldenrodyellow", fromHex("#fafad2"));
  public static final Color LIGHTGRAY = namedColor("lightgray", fromHex("#d3d3d3"));
  public static final Color LIGHTGREEN = namedColor("lightgreen", fromHex("#90ee90"));
  public static final Color LIGHTGREY = namedColor("lightgrey", fromHex("#d3d3d3"));
  public static final Color LIGHTPINK = namedColor("lightpink", fromHex("#ffb6c1"));
  public static final Color LIGHTSALMON = namedColor("lightsalmon", fromHex("#ffa07a"));
  public static final Color LIGHTSEAGREEN = namedColor("lightseagreen", fromHex("#20b2aa"));
  public static final Color LIGHTSKYBLUE = namedColor("lightskyblue", fromHex("#87cefa"));
  public static final Color LIGHTSLATEGRAY = namedColor("lightslategray", fromHex("#778899"));
  public static final Color LIGHTSLATEGREY = namedColor("lightslategrey", fromHex("#778899"));
  public static final Color LIGHTSTEELBLUE = namedColor("lightsteelblue", fromHex("#b0c4de"));
  public static final Color LIGHTYELLOW = namedColor("lightyellow", fromHex("#ffffe0"));
  public static final Color LIME = namedColor("lime", fromHex("#00ff00"));
  public static final Color LIMEGREEN = namedColor("limegreen", fromHex("#32cd32"));
  public static final Color LINEN = namedColor("linen", fromHex("#faf0e6"));
  public static final Color MAGENTA = namedColor("magenta", fromHex("#ff00ff"));
  public static final Color MAROON = namedColor("maroon", fromHex("#800000"));
  public static final Color MEDIUMAQUAMARINE = namedColor("mediumaquamarine", fromHex("#66cdaa"));
  public static final Color MEDIUMBLUE = namedColor("mediumblue", fromHex("#0000cd"));
  public static final Color MEDIUMORCHID = namedColor("mediumorchid", fromHex("#ba55d3"));
  public static final Color MEDIUMPURPLE = namedColor("mediumpurple", fromHex("#9370db"));
  public static final Color MEDIUMSEAGREEN = namedColor("mediumseagreen", fromHex("#3cb371"));
  public static final Color MEDIUMSLATEBLUE = namedColor("mediumslateblue", fromHex("#7b68ee"));
  public static final Color MEDIUMSPRINGGREEN = namedColor("mediumspringgreen", fromHex("#00fa9a"));
  public static final Color MEDIUMTURQUOISE = namedColor("mediumturquoise", fromHex("#48d1cc"));
  public static final Color MEDIUMVIOLETRED = namedColor("mediumvioletred", fromHex("#c71585"));
  public static final Color MIDNIGHTBLUE = namedColor("midnightblue", fromHex("#191970"));
  public static final Color MINTCREAM = namedColor("mintcream", fromHex("#f5fffa"));
  public static final Color MISTYROSE = namedColor("mistyrose", fromHex("#ffe4e1"));
  public static final Color MOCCASIN = namedColor("moccasin", fromHex("#ffe4b5"));
  public static final Color NAVAJOWHITE = namedColor("navajowhite", fromHex("#ffdead"));
  public static final Color NAVY = namedColor("navy", fromHex("#000080"));
  public static final Color OLDLACE = namedColor("oldlace", fromHex("#fdf5e6"));
  public static final Color OLIVE = namedColor("olive", fromHex("#808000"));
  public static final Color OLIVEDRAB = namedColor("olivedrab", fromHex("#6b8e23"));
  public static final Color ORANGE = namedColor("orange", fromHex("#ffa500"));
  public static final Color ORANGERED = namedColor("orangered", fromHex("#ff4500"));
  public static final Color ORCHID = namedColor("orchid", fromHex("#da70d6"));
  public static final Color PALEGOLDENROD = namedColor("palegoldenrod", fromHex("#eee8aa"));
  public static final Color PALEGREEN = namedColor("palegreen", fromHex("#98fb98"));
  public static final Color PALETURQUOISE = namedColor("paleturquoise", fromHex("#afeeee"));
  public static final Color PALEVIOLETRED = namedColor("palevioletred", fromHex("#db7093"));
  public static final Color PAPAYAWHIP = namedColor("papayawhip", fromHex("#ffefd5"));
  public static final Color PEACHPUFF = namedColor("peachpuff", fromHex("#ffdab9"));
  public static final Color PERU = namedColor("peru", fromHex("#cd853f"));
  public static final Color PINK = namedColor("pink", fromHex("#ffc0cb"));
  public static final Color PLUM = namedColor("plum", fromHex("#dda0dd"));
  public static final Color POWDERBLUE = namedColor("powderblue", fromHex("#b0e0e6"));
  public static final Color PURPLE = namedColor("purple", fromHex("#800080"));
  public static final Color RED = namedColor("red", fromHex("#ff0000"));
  public static final Color ROSYBROWN = namedColor("rosybrown", fromHex("#bc8f8f"));
  public static final Color ROYALBLUE = namedColor("royalblue", fromHex("#4169e1"));
  public static final Color SADDLEBROWN = namedColor("saddlebrown", fromHex("#8b4513"));
  public static final Color SALMON = namedColor("salmon", fromHex("#fa8072"));
  public static final Color SANDYBROWN = namedColor("sandybrown", fromHex("#f4a460"));
  public static final Color SEAGREEN = namedColor("seagreen", fromHex("#2e8b57"));
  public static final Color SEASHELL = namedColor("seashell", fromHex("#fff5ee"));
  public static final Color SIENNA = namedColor("sienna", fromHex("#a0522d"));
  public static final Color SILVER = namedColor("silver", fromHex("#c0c0c0"));
  public static final Color SKYBLUE = namedColor("skyblue", fromHex("#87ceeb"));
  public static final Color SLATEBLUE = namedColor("slateblue", fromHex("#6a5acd"));
  public static final Color SLATEGRAY = namedColor("slategray", fromHex("#708090"));
  public static final Color SLATEGREY = namedColor("slategrey", fromHex("#708090"));
  public static final Color SNOW = namedColor("snow", fromHex("#fffafa"));
  public static final Color SPRINGGREEN = namedColor("springgreen", fromHex("#00ff7f"));
  public static final Color STEELBLUE = namedColor("steelblue", fromHex("#4682b4"));
  public static final Color TAN = namedColor("tan", fromHex("#d2b48c"));
  public static final Color TEAL = namedColor("teal", fromHex("#008080"));
  public static final Color THISTLE = namedColor("thistle", fromHex("#d8bfd8"));
  public static final Color TOMATO = namedColor("tomato", fromHex("#ff6347"));
  public static final Color TURQUOISE = namedColor("turquoise", fromHex("#40e0d0"));
  public static final Color VIOLET = namedColor("violet", fromHex("#ee82ee"));
  public static final Color WHEAT = namedColor("wheat", fromHex("#f5deb3"));
  public static final Color WHITE = namedColor("white", fromHex("#ffffff"));
  public static final Color WHITESMOKE = namedColor("whitesmoke", fromHex("#f5f5f5"));
  public static final Color YELLOW = namedColor("yellow", fromHex("#ffff00"));
  public static final Color YELLOWGREEN = namedColor("yellowgreen", fromHex("#9acd32"));

  float r;
  float g;
  float b;
  float a;

  public Color(int r, int g, int b, int a) {
    this(r / 255f, g / 255f, b / 255f, a / 255f);
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

  /**
   * Allowed to parse hex strings in RGB/RGBA/RRGGBB/RRGGBBAA format.
   *
   * <p>Example
   *
   * @param value hex value
   * @return color
   */
  public static Color fromHex(@NonNull String value) {
    String hexValue = (value.startsWith("#") ? value.substring(1) : value).trim();
    var hex = Long.parseLong(hexValue, 16);
    return switch (hexValue.length()) {
      case 8 -> new Color(
          (int) ((hex >> 24) & 0xFF),
          (int) ((hex >> 16) & 0xFF),
          (int) ((hex >> 8) & 0xFF),
          (int) (hex & 0xFF));
      case 6 -> new Color(
          (int) ((hex >> 16) & 0xFF), (int) ((hex >> 8) & 0xFF), (int) (hex & 0xFF));
      case 4 -> new Color(
          (int) (((hex >> 12) & 0xF) * 0x11),
          (int) (((hex >> 8) & 0xF) * 0x11),
          (int) (((hex >> 4) & 0xF) * 0x11),
          (int) ((hex & 0xF) * 0x11));
      case 3 -> new Color(
          (int) (((hex >> 8) & 0xF) * 0x11),
          (int) (((hex >> 4) & 0xF) * 0x11),
          (int) ((hex & 0xF) * 0x11));
      default -> null;
    };
  }

  /**
   * Convert HSL values to a RGB Color.
   *
   * @param hue Hue is specified as degrees in the range 0 - 360.
   * @param saturation Saturation is specified as a percentage in the range 1 - 100.
   * @param lightness lightness is specified as a percentage in the range 1 - 100.
   * @param alpha the alpha value between 0 - 1
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

  private static float hueToRGB(float p, float q, float h) {
    float hh = h;
    if (hh < 0) hh += 1;
    if (hh > 1) hh -= 1;
    if (6 * hh < 1) return p + ((q - p) * 6 * hh);
    if (2 * hh < 1) return q;
    if (3 * hh < 2) return p + ((q - p) * 6 * ((2.0f / 3.0f) - hh));
    return p;
  }

  /**
   * Returns color by name or null if there is no static mapping. Color name converted to lower case
   * before search.
   *
   * @param name color name to search.
   * @return color or null.
   */
  public static Color get(@NonNull String name) {
    return colors.get(name.toLowerCase());
  }

  /**
   * Used to put color with specified name (in lower case) to static mapping.
   *
   * @param name color name.
   * @param color color.
   */
  public static void put(@NonNull String name, @NonNull Color color) {
    colors.put(name.toLowerCase(Locale.ROOT), color);
  }

  /**
   * Checks if there is static mapping for color by color name.
   *
   * @param colorName color name.
   * @return true if static mapping exists.
   */
  public static boolean exists(@NonNull String colorName) {
    return colors.containsKey(colorName.toLowerCase());
  }

  /**
   * Puts color to mapping with specified name (in lower case) and returns provided color.
   *
   * @param name name.
   * @param color color.
   * @return provided color.
   */
  private static Color namedColor(@NonNull String name, @NonNull Color color) {
    put(name, color);
    return color;
  }

  public static Color random() {
    int index = new Random(System.currentTimeMillis()).nextInt(colors.size());
    return new ArrayList<>(colors.values()).get(index);
  }

  @Override
  public String toString() {
    return "rgba(" + r + ", " + g + ", " + b + ", " + a + ")";
  }

  public String hexString() {
    int rr = (int) r * 255;
    int gg = (int) (g * 255);
    int bb = (int) (b * 255);
    int aa = (int) (a * 255);
    return String.format("#%02x%02x%02x%02x", rr, gg, bb, aa);
  }

//    return Integer.toHexString((int) (r * 255));
}
