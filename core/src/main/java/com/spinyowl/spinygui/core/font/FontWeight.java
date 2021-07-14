package com.spinyowl.spinygui.core.font;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/** CSS font weight. */
@Getter
@ToString
@EqualsAndHashCode(exclude = "name")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class FontWeight {

  private static final Map<String, FontWeight> VALUES = new HashMap<>();

  // @formatter:off
  public static final FontWeight THIN = FontWeight.create(100, "thin");
  public static final FontWeight ULTRA_LIGHT = FontWeight.create(200, "ultra light", "ultralight");
  public static final FontWeight EXTRA_LIGHT = FontWeight.create(200, "extra light", "extralight");
  public static final FontWeight LIGHT = FontWeight.create(300, "light");
  public static final FontWeight NORMAL = FontWeight.create(400, "normal");
  public static final FontWeight BOOK = FontWeight.create(400, "book");
  public static final FontWeight REGULAR = FontWeight.create(400, "regular");
  public static final FontWeight MEDIUM = FontWeight.create(500, "medium");
  public static final FontWeight SEMI_BOLD = FontWeight.create(600, "semi bold", "semibold");
  public static final FontWeight DEMI_BOLD = FontWeight.create(600, "demi bold", "demibold");
  public static final FontWeight BOLD = FontWeight.create(700, "bold");
  public static final FontWeight EXTRA_BOLD = FontWeight.create(800, "extra bold", "extrabold");
  public static final FontWeight ULTRA_BOLD = FontWeight.create(800, "ultra bold", "ultrabold");
  public static final FontWeight HEAVY = FontWeight.create(900, "heavy");
  public static final FontWeight BLACK = FontWeight.create(900, "black");
  // @formatter:on

  /** Font weight. */
  private final int weight;

  /** Name of font weight type (should be same as in css specification). */
  @NonNull private final String name;

  /**
   * Used to create new font weight element with specified name. Note that name will be converted to
   * lower case and it should be the same as names of css font weight property in css specification.
   *
   * @param name name of font weight element.
   * @return new font weight element (or existing one).
   */
  public static FontWeight create(int weight, @NonNull String name, String... aliases) {
    Objects.requireNonNull(name);
    var fontWeight = VALUES.computeIfAbsent(name.toLowerCase(), key -> new FontWeight(weight, key));
    if (aliases != null) {
      for (@NonNull String alias : aliases) {
        VALUES.put(alias.toLowerCase(), new FontWeight(weight, alias));
      }
    }
    return fontWeight;
  }

  /**
   * Used to find font weight element with specified name. Note that name will be converted to lower
   * case and it should be the same as names of css font weight property in css specification.
   *
   * @param name name of font weight element.
   * @return existing font weight element or null.
   */
  public static FontWeight find(@NonNull String name) {
    return VALUES.get(name.toLowerCase());
  }

  public static FontWeight find(int value) {
    return switch (Math.round(value / 100f)) {
      case 1 -> THIN;
      case 2 -> EXTRA_LIGHT;
      case 3 -> LIGHT;
      case 5 -> MEDIUM;
      case 6 -> SEMI_BOLD;
      case 7 -> BOLD;
      case 8 -> EXTRA_BOLD;
      case 9 -> BLACK;
      default -> NORMAL;
    };
  }

  /**
   * Returns set of all available font weight values.
   *
   * @return set of all available font weight values.
   */
  public static Set<FontWeight> values() {
    return Set.copyOf(VALUES.values());
  }

  /**
   * Returns true if there is a font weight value wth specified name.
   *
   * @param name font weight name.
   * @return true if there is a font weight value wth specified name.
   */
  public static boolean contains(@NonNull String name) {
    return values().stream().map(FontWeight::name).anyMatch(v -> v.equalsIgnoreCase(name));
  }
}
