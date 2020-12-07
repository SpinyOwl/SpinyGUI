package com.spinyowl.spinygui.core.font;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;

public final class FontStyle {

  private static final Map<String, FontStyle> VALUES = new ConcurrentHashMap<>();

  //@formatter:off
  public static final FontStyle NORMAL  = FontStyle.create("normal");
  public static final FontStyle ITALIC  = FontStyle.create("italic");
  public static final FontStyle OBLIQUE = FontStyle.create("oblique");
  //@formatter:on


  /**
   * Name of font style type (should be same as in css specification)
   */
  private final String name;

  /**
   * Creates font style element with specified name.
   *
   * @param name name of font style type (should be same as in css specification)
   */
  private FontStyle(String name) {
    this.name = name;
  }

  /**
   * Used to find font style element with specified name. Note that name will be converted to lower
   * case and it should be the same as names of css font style property in css specification.
   *
   * @param name name of font style element.
   * @return existing font style element or null.
   */
  public static FontStyle find(String name) {
    Objects.requireNonNull(name);
    return VALUES.get(name.toLowerCase());
  }

  /**
   * Used to create new font style element with specified name. Note that name will be converted to
   * lower case and it should be the same as names of css font style property in css specification.
   *
   * @param name name of font style element.
   * @return new font style element (or existing one).
   */
  public static FontStyle create(String name) {
    Objects.requireNonNull(name);
    return VALUES.computeIfAbsent(name.toLowerCase(), FontStyle::new);
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
  public static boolean contains(String name) {
    if (name == null) {
      return false;
    }
    return values().stream().map(FontStyle::getName)
        .anyMatch(v -> v.equalsIgnoreCase(name));
  }

  /**
   * Name of font style.
   *
   * @return name of font style.
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
    FontStyle fontStyle = (FontStyle) o;
    return Objects.equals(name, fontStyle.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", FontStyle.class.getSimpleName() + "[", "]")
        .add("name='" + name + "'")
        .toString();
  }
}
