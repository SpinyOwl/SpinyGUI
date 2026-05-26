package com.spinyowl.spinygui.core.style.types;

import static lombok.AccessLevel.PRIVATE;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

/** CSS display. Currently supports */
@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = PRIVATE)
public final class Display {

  private static final Map<String, Display> VALUES = new HashMap<>();

  /** Displays an element as a block-level flex container. */
  public static final Display FLEX = Display.create("flex");

  /** The element is completely removed. */
  public static final Display NONE = Display.create("none");

  /**
   * The element generates a block element box, generating line breaks both before and after the
   * element when in the normal flow.
   */
  public static final Display BLOCK = Display.create("block");

  /**
   * The element generates one or more inline element boxes that do not generate line breaks before
   * or after themselves. In normal flow, the next element will be on the same line if there is
   * space.
   */
  public static final Display INLINE = Display.create("inline");

  /** Name of display type (should be same as in css specification) */
  @NonNull private final String name;

  /**
   * Used to create new display element with specified name. Note that name will be converted to
   * lower case, and it should be the same as names of css Display property in css specification.
   *
   * @param name name of display element.
   * @return new Display element (or existing one).
   */
  public static Display create(@NonNull String name) {
    return VALUES.computeIfAbsent(name.toLowerCase(), Display::new);
  }

  /**
   * Used to find display element with specified name. Note that name will be converted to lower
   * case, and it should be the same as names of css Display property in css specification.
   *
   * @param name name of display element.
   * @return existing Display element or null.
   */
  public static Display find(@NonNull String name) {
    return VALUES.get(name.toLowerCase());
  }

  /**
   * Returns set of all available display values.
   *
   * @return set of all available display values.
   */
  public static Set<Display> values() {
    return Set.copyOf(VALUES.values());
  }

  /**
   * Returns true if there is a display value wth specified name.
   *
   * @param name display name.
   * @return true if there is a display value wth specified name.
   */
  public static boolean contains(@NonNull String name) {
    return VALUES.containsKey(name.toLowerCase());
  }

  @Override
  public String toString() {
    return name;
  }
}
