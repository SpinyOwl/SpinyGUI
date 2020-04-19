package com.spinyowl.spinygui.core.style.types.border;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CSS border style.
 */
public final class BorderStyle {

  private static final Map<String, BorderStyle> VALUES = new ConcurrentHashMap<>();

  /**
   * Specifies no border. This is default.
   */
  public static final BorderStyle NONE = BorderStyle.create("none");

  /**
   * The same as "none", except in border conflict resolution for table elements.
   */
  public static final BorderStyle HIDDEN = BorderStyle.create("hidden");

  /**
   * Specifies a dotted border.
   */
  public static final BorderStyle DOTTED = BorderStyle.create("dotted");

  /**
   * Specifies a dashed border.
   */
  public static final BorderStyle DASHED = BorderStyle.create("dashed");

  /**
   * Specifies a solid border.
   */
  public static final BorderStyle SOLID = BorderStyle.create("solid");

  /**
   * Specifies a double border.
   */
  public static final BorderStyle DOUBLE = BorderStyle.create("double");

  /**
   * Specifies a 3D grooved border. The effect depends on the border-color value.
   */
  public static final BorderStyle GROOVE = BorderStyle.create("groove");

  /**
   * Specifies a 3D ridged border. The effect depends on the border-color value.
   */
  public static final BorderStyle RIDGE = BorderStyle.create("ridge");

  /**
   * Specifies a 3D inset border. The effect depends on the border-color value.
   */
  public static final BorderStyle INSET = BorderStyle.create("inset");

  /**
   * Specifies a 3D outset border. The effect depends on the border-color value.
   */
  public static final BorderStyle OUTSET = BorderStyle.create("outset");

  /**
   * Name of border style type (should be same as in css specification)
   */
  private final String name;

  /**
   * Creates border style element with specified name.
   *
   * @param name name of border style type (should be same as in css specification)
   */
  private BorderStyle(String name) {
    this.name = name;
  }

  /**
   * Used to create new border style element with specified name. Note that name will be converted
   * to lower case and it should be the same as names of css border style property in css
   * specification.
   *
   * @param name name of border style element.
   * @return new border style element (or existing one).
   */
  public static BorderStyle create(String name) {
    Objects.requireNonNull(name);
    return VALUES.computeIfAbsent(name.toLowerCase(), BorderStyle::new);
  }

  /**
   * Used to find border style element with specified name. Note that name will be converted to
   * lower case and it should be the same as names of css border style property in css
   * specification.
   *
   * @param name name of border style element.
   * @return existing border element or null.
   */
  public static BorderStyle find(String name) {
    Objects.requireNonNull(name);
    return VALUES.get(name.toLowerCase());
  }

  /**
   * Returns set of all available border style values.
   *
   * @return set of all available border style values.
   */
  public static Set<BorderStyle> values() {
    return Set.copyOf(VALUES.values());
  }

  /**
   * Returns true there is a border style value wth specified name.
   *
   * @param name border style name.
   * @return true there is a border style value wth specified name.
   */
  public static boolean contains(String name) {
    if (name == null) {
      return false;
    }
    return values().stream().map(BorderStyle::getName)
      .anyMatch(v -> v.equalsIgnoreCase(name));
  }

  /**
   * Name of border style.
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
    BorderStyle borderStyle = (BorderStyle) o;
    return Objects.equals(name, borderStyle.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", BorderStyle.class.getSimpleName() + "[", "]")
      .add("name='" + name + "'")
      .toString();
  }
}
