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

/** CSS font stretch. */
@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class FontStretch {

  private static final Map<String, FontStretch> VALUES = new HashMap<>();

  // @formatter:off
  public static final FontStretch ULTRA_CONDENSED = FontStretch.create("ultra-condensed");
  public static final FontStretch EXTRA_CONDENSED = FontStretch.create("extra-condensed");
  public static final FontStretch CONDENSED = FontStretch.create("condensed");
  public static final FontStretch SEMI_CONDENSED = FontStretch.create("semi-condensed");
  public static final FontStretch NORMAL = FontStretch.create("normal");
  public static final FontStretch SEMI_EXPANDED = FontStretch.create("semi-expanded");
  public static final FontStretch EXPANDED = FontStretch.create("expanded");
  public static final FontStretch EXTRA_EXPANDED = FontStretch.create("extra-expanded");
  public static final FontStretch ULTRA_EXPANDED = FontStretch.create("ultra-expanded");
  // @formatter:on

  /** Name of font stretch type (should be same as in css specification) */
  @NonNull private final String name;

  /**
   * Used to create new font stretch element with specified name. Note that name will be converted
   * to lower case and it should be the same as names of css font stretch property in css
   * specification.
   *
   * @param name name of font stretch element.
   * @return new font stretch element (or existing one).
   */
  public static FontStretch create(@NonNull String name) {
    return VALUES.computeIfAbsent(name.toLowerCase(), FontStretch::new);
  }

  /**
   * Used to find font stretch element with specified name. Note that name will be converted to
   * lower case and it should be the same as names of css font stretch property in css
   * specification.
   *
   * @param name name of font stretch element.
   * @return existing font stretch element or null.
   */
  public static FontStretch find(@NonNull String name) {
    return VALUES.get(name.toLowerCase());
  }

  /**
   * Returns set of all available font stretch values.
   *
   * @return set of all available font stretch values.
   */
  public static Set<FontStretch> values() {
    return Set.copyOf(VALUES.values());
  }

  /**
   * Returns true if there is a font stretch value wth specified name.
   *
   * @param name font stretch name.
   * @return true if there is a font stretch value wth specified name.
   */
  public static boolean contains(@NonNull String name) {
    return values().stream().map(FontStretch::name).anyMatch(v -> v.equalsIgnoreCase(name));
  }
}
