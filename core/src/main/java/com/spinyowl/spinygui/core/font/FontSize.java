package com.spinyowl.spinygui.core.font;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class FontSize {

  /** Stores all existing dictionary values. */
  private static final Map<String, FontSize> VALUES = new ConcurrentHashMap<>();

  public static final FontSize XX_LARGE = FontSize.create("xx-large", 6);
  public static final FontSize X_LARGE = FontSize.create("x-large", 4);
  public static final FontSize LARGE = FontSize.create("large", 2);
  public static final FontSize MEDIUM = FontSize.create("medium", 0);
  public static final FontSize SMALL = FontSize.create("small", 2);
  public static final FontSize X_SMALL = FontSize.create("x-small", 4);
  public static final FontSize XX_SMALL = FontSize.create("xx-small", 6);

  // TODO: Move this default font size to settings.
  private static int defaultFontSize = 16;

  /** Name of FontSize element. */
  @Getter private final String name;

  private final int sizeModifier;

  /**
   * Creates FontSize element with specified name.
   *
   * @param name name of FontSize type.
   * @param sizeModifier font size in pixels.
   */
  private FontSize(@NonNull String name, int sizeModifier) {
    this.name = name;
    this.sizeModifier = sizeModifier;
  }

  public static int defaultFontSize() {
    return defaultFontSize;
  }

  public static void defaultFontSize(int defaultFontSize) {
    FontSize.defaultFontSize = defaultFontSize;
  }

  /**
   * Used to create new FontSize element with specified name. Note that name will be converted to
   * lower case.
   *
   * @param name name of FontSize element.
   * @param size font size in pixels.
   * @return new FontSize element (or existing one).
   */
  public static FontSize create(@NonNull String name, int size) {
    return VALUES.computeIfAbsent(
        Objects.requireNonNull(name).toLowerCase(), name1 -> new FontSize(name1, size));
  }

  /**
   * Used to find FontSize element with specified name. Note that name will be converted to lower
   * case.
   *
   * @param name name of FontSize element.
   * @return existing FontSize element or null.
   */
  public static FontSize find(@NonNull String name) {
    return VALUES.get(Objects.requireNonNull(name).toLowerCase());
  }

  /**
   * Returns set of all available FontSize values.
   *
   * @return set of all available FontSize values.
   */
  public static Set<FontSize> values() {
    return Set.copyOf(VALUES.values());
  }

  /**
   * Returns true if there is a FontSize value wth specified name.
   *
   * @param name FontSize name.
   * @return true if there is a FontSize value wth specified name.
   */
  public static boolean contains(@NonNull String name) {
    return VALUES.containsKey(name.toLowerCase());
  }

  public int size() {
    return defaultFontSize() + sizeModifier;
  }
}
