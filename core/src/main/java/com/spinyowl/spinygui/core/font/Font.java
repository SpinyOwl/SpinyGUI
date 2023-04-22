package com.spinyowl.spinygui.core.font;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@RequiredArgsConstructor
public class Font {
  private static final Map<String, List<Font>> fontFamilies = new HashMap<>();

  private static final String ROBOTO = "Roboto";

  public static final Font ROBOTO_LIGHT =
      Font.addFont(
          new Font(
              ROBOTO,
              FontStyle.NORMAL,
              FontStretch.NORMAL,
              FontWeight.LIGHT,
              "fonts/Roboto-Light.ttf"));

  public static final Font ROBOTO_BOLD =
      Font.addFont(
          new Font(
              ROBOTO,
              FontStyle.NORMAL,
              FontStretch.NORMAL,
              FontWeight.BOLD,
              "fonts/Roboto-Bold.ttf"));

  public static final Font ROBOTO_REGULAR =
      Font.addFont(
          new Font(
              ROBOTO,
              FontStyle.NORMAL,
              FontStretch.NORMAL,
              FontWeight.REGULAR,
              "fonts/Roboto-Regular.ttf"));
  public static final Font DEFAULT = ROBOTO_REGULAR;

  @NonNull private final String fontFamily;
  @NonNull private final FontStyle style;
  @NonNull private final FontStretch stretch;
  @NonNull private final FontWeight weight;

  @NonNull private final String path;

  public Font(@NonNull String fontFamily, @NonNull String path) {
    this(fontFamily, FontStyle.NORMAL, FontStretch.NORMAL, FontWeight.NORMAL, path);
  }

  public Font(@NonNull String fontFamily, @NonNull FontStyle style, @NonNull String path) {
    this(fontFamily, style, FontStretch.NORMAL, FontWeight.NORMAL, path);
  }

  public static Font addFont(@NonNull Font font) {
    List<Font> fonts = fontFamilies.computeIfAbsent(font.fontFamily(), n -> new LinkedList<>());
    if (hasFont(font.fontFamily, font.style, font.weight, font.stretch)) {
      if (log.isWarnEnabled()) {
        log.warn("Font '{}' will be replaced.", font.fontFamily());
      }
      fonts.removeAll(getFonts(font.fontFamily, font.style, font.weight, font.stretch));
    }
    fonts.add(font);
    return font;
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
   * @param name font family name.
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
   * @param name font family name.
   * @param style font style
   * @return obtained font.
   */
  public static List<Font> getFonts(String name, FontStyle style) {
    return getFonts(name, style, null, null);
  }

  /**
   * Search for fonts with specified parameters. Any parameter could be nullable. In this case this
   * parameter will not be used during search.
   *
   * @param name font family name.
   * @param style font style
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
   * @param name font family name.
   * @param style font style
   * @param width font width
   * @param weight font weight
   * @return obtained font.
   */
  public static List<Font> getFonts(
      String name, FontStyle style, FontWeight weight, FontStretch width) {
    List<Font> fonts = fontFamilies.get(name);
    return fonts.stream().filter(font -> checkFont(font, name, style, weight, width)).toList();
  }

  public static Set<Font> findFonts(
      Set<String> fontFamilies, FontStyle fontStyle, FontWeight fontWeight) {
    return fontFamilies.stream()
        .map(f -> Font.getFonts(f, fontStyle, fontWeight))
        .flatMap(Collection::stream)
        .collect(Collectors.toSet());
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
   * @param name font family name.
   * @param weight font weight
   * @return true if there is any font with specified parameters.
   */
  public static boolean hasFont(String name, FontWeight weight) {
    return hasFont(name, null, weight, null);
  }

  /**
   * Returns true if there is any font with specified parameters.
   *
   * @param name font family name.
   * @param style font style
   * @param weight font weight
   * @return true if there is any font with specified parameters.
   */
  public static boolean hasFont(String name, FontStyle style, FontWeight weight) {
    return hasFont(name, style, weight, null);
  }

  /**
   * Returns true if there is any font with specified parameters.
   *
   * @param name font family name.
   * @param style font style
   * @return true if there is any font with specified parameters.
   */
  public static boolean hasFont(String name, FontStyle style) {
    return hasFont(name, style, null, null);
  }

  /**
   * Returns true if there is any font with specified parameters. Any parameter could be nullable.
   * In this case this parameter will not be used during search.
   *
   * @param name font family name.
   * @param style font style
   * @param width font width
   * @param weight font weight
   * @return true if there is any font with specified parameters.
   */
  public static boolean hasFont(
      String name, FontStyle style, FontWeight weight, FontStretch width) {
    return fontFamilies.containsKey(name)
        && fontFamilies.get(name).stream().anyMatch(k -> checkFont(k, name, style, weight, width));
  }

  private static boolean checkFont(
      Font font, String name, FontStyle style, FontWeight weight, FontStretch width) {
    return (name == null || name.equalsIgnoreCase(font.fontFamily))
        && (style == null || style.equals(font.style))
        && (weight == null || weight.equals(font.weight))
        && (width == null || width.equals(font.stretch));
  }
}
