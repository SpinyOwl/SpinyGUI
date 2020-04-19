package com.spinyowl.spinygui.core.font;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@RequiredArgsConstructor
public class Font {

  private static Map<FontKey, Font> fonts = new ConcurrentHashMap<>();

  @NonNull
  private final String name;

  @NonNull
  private final FontStyle style;

  @NonNull
  private final FontStretch width;

  @NonNull
  private final FontWeight weight;

  @NonNull
  private final String path;

  public Font(String name, String path) {
    this(name, FontStyle.NORMAL, FontStretch.NORMAL, FontWeight.NORMAL, path);
  }

  public Font(String name, FontStyle style, String path) {
    this(name, style, FontStretch.NORMAL, FontWeight.NORMAL, path);
  }

  public static void addFont(Font font) {
    Objects.requireNonNull(font);

    var key = new FontKey(font.name(), font.style(), font.width(), font.weight());

    if (fonts.containsKey(key)) {
      LOGGER.warn("Font '{}' will be replaced.", font.name());
    }
    fonts.put(key, font);
  }

  /**
   * Search for fonts with specified font family name.
   *
   * @param name font family name.
   * @return obtained font.
   */
  public static List<Font> getFonts(String name) {
    return getFonts(name, null, null, null);
  }

  /**
   * Search for fonts with specified parameters. Any parameter could be nullable. In this case this
   * parameter will not be used during search.
   *
   * @param name   font family name.
   * @param weight font weight
   * @return obtained font.
   */
  public static List<Font> getFonts(String name, FontWeight weight) {
    return getFonts(name, null, weight, null);
  }

  /**
   * Search for fonts with specified parameters. Any parameter could be nullable. In this case this
   * parameter will not be used during search.
   *
   * @param name   font family name.
   * @param style  font style
   * @param weight font weight
   * @return obtained font.
   */
  public static List<Font> getFonts(String name, FontStyle style, FontWeight weight) {
    return getFonts(name, style, weight, null);
  }

  /**
   * Search for fonts with specified parameters. Any parameter could be nullable. In this case this
   * parameter will not be used during search.
   *
   * @param name  font family name.
   * @param style font style
   * @return obtained font.
   */
  public static List<Font> getFonts(String name, FontStyle style) {
    return getFonts(name, style, null, null);
  }

  /**
   * Returns true if there is any font with specified parameters.
   *
   * @param name font family name.
   * @return true if there is any font with specified parameters.
   */
  public static boolean hasFont(String name) {
    return hasFont(name, null, null, null);
  }

  /**
   * Returns true if there is any font with specified parameters.
   *
   * @param name   font family name.
   * @param weight font weight
   * @return true if there is any font with specified parameters.
   */
  public static boolean hasFont(String name, FontWeight weight) {
    return hasFont(name, null, weight, null);
  }

  /**
   * Returns true if there is any font with specified parameters.
   *
   * @param name   font family name.
   * @param style  font style
   * @param weight font weight
   * @return true if there is any font with specified parameters.
   */
  public static boolean hasFont(String name, FontStyle style, FontWeight weight) {
    return hasFont(name, style, weight, null);
  }

  /**
   * Returns true if there is any font with specified parameters.
   *
   * @param name  font family name.
   * @param style font style
   * @return true if there is any font with specified parameters.
   */
  public static boolean hasFont(String name, FontStyle style) {
    return hasFont(name, style, null, null);
  }


  /**
   * Search for fonts with specified parameters. Any parameter could be nullable. In this case this
   * parameter will not be used during search.
   *
   * @param name   font family name.
   * @param style  font style
   * @param width  font width
   * @param weight font weight
   * @return obtained font.
   */
  public static List<Font> getFonts(
      String name, FontStyle style, FontWeight weight, FontStretch width
  ) {
    return fonts.keySet().stream()
        .filter(k -> checkFont(k, name, style, weight, width))
        .map(fonts::get).collect(Collectors.toList());
  }

  /**
   * Returns true if there is any font with specified parameters. Any parameter could be nullable.
   * In this case this parameter will not be used during search.
   *
   * @param name   font family name.
   * @param style  font style
   * @param width  font width
   * @param weight font weight
   * @return true if there is any font with specified parameters.
   */
  public static boolean hasFont(
      String name, FontStyle style, FontWeight weight, FontStretch width
  ) {
    return fonts.keySet().stream().anyMatch(k -> checkFont(k, name, style, weight, width));
  }

  private static boolean checkFont(
      FontKey fontKey, String name, FontStyle style, FontWeight weight, FontStretch width
  ) {
    return (name == null || name.equalsIgnoreCase(fontKey.name)) &&
        (style == null || style.equals(fontKey.style)) &&
        (weight == null || weight.equals(fontKey.weight)) &&
        (width == null || width.equals(fontKey.width));
  }

  @Data
  @RequiredArgsConstructor
  private static class FontKey {

    @NonNull
    private final String name;

    @NonNull
    private final FontStyle style;

    @NonNull
    private final FontStretch width;

    @NonNull
    private final FontWeight weight;
  }
}
