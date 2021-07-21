package com.spinyowl.spinygui.core.style.types;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/** CSS white-space. */
@Getter
@EqualsAndHashCode
public final class WhiteSpace {

  private static final Map<String, WhiteSpace> VALUES = new HashMap<>();

  /**
   * Sequences of whitespace will collapse into a single whitespace. Text will wrap when necessary.
   * This is default.
   */
  public static final WhiteSpace NORMAL = WhiteSpace.create("normal");
  /**
   * Sequences of whitespace will collapse into a single whitespace. Text will never wrap to the
   * next line. The text continues on the same line until a <br>
   * tag is encountered.
   */
  public static final WhiteSpace NOWRAP = WhiteSpace.create("nowrap");
  /**
   * Whitespace is preserved by the browser. Text will only wrap on line breaks. Acts like the
   * {@code <pre>} tag in HTML.
   */
  public static final WhiteSpace PRE = WhiteSpace.create("pre");
  /**
   * Sequences of whitespace will collapse into a single whitespace. Text will wrap when necessary,
   * and on line breaks.
   */
  public static final WhiteSpace PRE_LINE = WhiteSpace.create("pre-line");
  /** Whitespace is preserved by the browser. Text will wrap when necessary, and on line breaks. */
  public static final WhiteSpace PRE_WRAP = WhiteSpace.create("pre-wrap");

  /** Name of white-space type (should be same as in css specification) */
  private final String name;

  /**
   * Creates white-space element with specified name.
   *
   * @param name name of white-space type (should be same as in css specification)
   */
  private WhiteSpace(String name) {
    this.name = name;
  }

  /**
   * Used to create new white-space element with specified name. Note that name will be converted to
   * lower case and it should be the same as names of css white-space property in css specification.
   *
   * @param name name of white-space element.
   * @return new white-space element (or existing one).
   */
  public static WhiteSpace create(String name) {
    Objects.requireNonNull(name);
    return VALUES.computeIfAbsent(name.toLowerCase(), WhiteSpace::new);
  }

  /**
   * Used to find white-space element with specified name. Note that name will be converted to lower
   * case and it should be the same as names of css white-space property in css specification.
   *
   * @param name name of white-space element.
   * @return existing white-space element or null.
   */
  public static WhiteSpace find(String name) {
    Objects.requireNonNull(name);
    return VALUES.get(name.toLowerCase());
  }

  /**
   * Returns set of all available white-space values.
   *
   * @return set of all available white-space values.
   */
  public static Set<WhiteSpace> values() {
    return Set.copyOf(VALUES.values());
  }

  /**
   * Returns true if there is a white-space value wth specified name.
   *
   * @param name white-space name.
   * @return true if there is a white-space value wth specified name.
   */
  public static boolean contains(String name) {
    if (name == null) {
      return false;
    }
    return values().stream().map(WhiteSpace::name).anyMatch(v -> v.equalsIgnoreCase(name));
  }

  @Override
  public String toString() {
    return name;
  }
}
