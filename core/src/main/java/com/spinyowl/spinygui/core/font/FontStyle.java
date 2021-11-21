package com.spinyowl.spinygui.core.font;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class FontStyle {

  private static final Map<String, FontStyle> VALUES = new HashMap<>();

  // @formatter:off
  public static final FontStyle NORMAL = FontStyle.create("normal", "regular");
  public static final FontStyle ITALIC = FontStyle.create("italic");
  public static final FontStyle OBLIQUE = FontStyle.create("oblique", "slanted");
  // @formatter:on

  /** Name of font style type (should be same as in css specification) */
  @NonNull private final String name;

  @NonNull private final String[] aliases;

  /**
   * Used to find font style element with specified name. Note that name will be converted to lower
   * case and it should be the same as names of css font style property in css specification.
   *
   * @param name name of font style element.
   * @return existing font style element or null.
   */
  public static FontStyle find(@NonNull String name) {
    return VALUES.get(name.toLowerCase());
  }

  /**
   * Used to create new font style element with specified name. Note that name will be converted to
   * lower case and it should be the same as names of css font style property in css specification.
   *
   * @param name name of font style element.
   * @return new font style element (or existing one).
   */
  public static FontStyle create(@NonNull String name, String... aliases) {
    String[] als = aliases == null ? new String[0] : aliases;
    var fontStyle = VALUES.computeIfAbsent(name.toLowerCase(), n -> new FontStyle(name, als));
    for (@NonNull String alias : als) {
      VALUES.put(alias.toLowerCase(), fontStyle);
    }
    return fontStyle;
  }

  /**
   * Returns set of all available font style values.
   *
   * @return set of all available font style values.
   */
  public static Set<FontStyle> values() {
    return Set.copyOf(VALUES.values());
  }

  /**
   * Returns true if there is a font style value wth specified name.
   *
   * @param name font style name.
   * @return true if there is a font style value wth specified name.
   */
  public static boolean contains(@NonNull String name) {
    return VALUES.containsKey(name.toLowerCase());
  }
}
