package com.spinyowl.spinygui.core.font;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CSS font stretch.
 */
public final class FontStretch {

  private static final Map<String, FontStretch> VALUES = new ConcurrentHashMap<>();

  //@formatter:off
  public static final FontStretch ULTRA_CONDENSED  = FontStretch.create("ultra-condensed");
  public static final FontStretch EXTRA_CONDENSED  = FontStretch.create("extra-condensed");
  public static final FontStretch CONDENSED        = FontStretch.create("condensed");
  public static final FontStretch SEMI_CONDENSED   = FontStretch.create("semi-condensed");
  public static final FontStretch NORMAL           = FontStretch.create("normal");
  public static final FontStretch SEMI_EXPANDED    = FontStretch.create("semi-expanded");
  public static final FontStretch EXPANDED         = FontStretch.create("expanded");
  public static final FontStretch EXTRA_EXPANDED   = FontStretch.create("extra-expanded");
  public static final FontStretch ULTRA_EXPANDED   = FontStretch.create("ultra-expanded");
  //@formatter:on

  /**
   * Name of font stretch type (should be same as in css specification)
   */
  private final String name;

  /**
   * Creates font stretch element with specified name.
   *
   * @param name name of font stretch type (should be same as in css specification)
   */
  private FontStretch(String name) {
    this.name = name;
  }


  /**
   * Used to create new font stretch element with specified name. Note that name will be converted
   * to lower case and it should be the same as names of css font stretch property in css
   * specification.
   *
   * @param name name of font stretch element.
   * @return new font stretch element (or existing one).
   */
  public static FontStretch create(String name) {
    Objects.requireNonNull(name);
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
  public static FontStretch find(String name) {
    Objects.requireNonNull(name);
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
   * Returns true there is a font stretch value wth specified name.
   *
   * @param name font stretch name.
   * @return true there is a font stretch value wth specified name.
   */
  public static boolean contains(String name) {
    if (name == null) {
      return false;
    }
    return values().stream().map(FontStretch::getName)
        .anyMatch(v -> v.equalsIgnoreCase(name));
  }

  /**
   * Name of font stretch.
   *
   * @return
   */
  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FontStretch fontStretch = (FontStretch) o;
    return Objects.equals(name, fontStretch.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", FontStretch.class.getSimpleName() + "[", "]")
        .add("name='" + name + "'")
        .toString();
  }
}
