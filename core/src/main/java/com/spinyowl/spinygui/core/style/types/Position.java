package com.spinyowl.spinygui.core.style.types;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

/** CSS position. */
@Getter
@EqualsAndHashCode
public final class Position {

  private static final Map<String, Position> VALUES = new HashMap<>();

  /** The element is positioned relative to its first positioned (not static) ancestor element. */
  public static final Position ABSOLUTE = Position.create("absolute");
  /**
   * The element is positioned relative to its normal position, so "left:20px" adds 20 pixels to the
   * element's LEFT position.
   */
  public static final Position RELATIVE = Position.create("relative");
  /**
   * Static positioned elements are not affected by the top, bottom, left, and right properties.
   * Elements render in order, as they appear in the document flow.
   */
  public static final Position STATIC = Position.create("static");

  // NEXT POSITION OPTIONS ARE CURRENTLY NOT IMPLEMENTED
  //  /**
  //   * The element is positioned relative to the browser window.
  //   */
  //  public static final Position FIXED = Position.create("fixed");

  /** Name of position type (should be same as in css specification) */
  @NonNull
  private final String name;

  /**
   * Creates position element with specified name.
   *
   * @param name name of position type (should be same as in css specification)
   */
  private Position(@NonNull String name) {
    this.name = name;
  }

  /**
   * Used to create new position element with specified name. Note that name will be converted to
   * lower case and it should be the same as names of css position property in css specification.
   *
   * @param name name of position element.
   * @return new position element (or existing one).
   */
  public static Position create(@NonNull String name) {
    Objects.requireNonNull(name);
    return VALUES.computeIfAbsent(name.toLowerCase(), Position::new);
  }

  /**
   * Used to find position element with specified name. Note that name will be converted to lower
   * case and it should be the same as names of css position property in css specification.
   *
   * @param name name of position element.
   * @return existing Position element or null.
   */
  public static Position find(@NonNull String name) {
    Objects.requireNonNull(name);
    return VALUES.get(name.toLowerCase());
  }

  /**
   * Returns set of all available position values.
   *
   * @return set of all available position values.
   */
  public static Set<Position> values() {
    return Set.copyOf(VALUES.values());
  }

  /**
   * Returns true if there is a position value wth specified name.
   *
   * @param name position name.
   * @return true if there is a position value wth specified name.
   */
  public static boolean contains(@NonNull String name) {
    return VALUES.containsKey(name.toLowerCase());
  }

  @Override
  public String toString() {
    return name;
  }
}
