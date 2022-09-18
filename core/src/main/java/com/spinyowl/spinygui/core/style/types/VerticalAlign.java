package com.spinyowl.spinygui.core.style.types;

import static lombok.AccessLevel.PRIVATE;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = PRIVATE)
public class VerticalAlign {

  /** Stores all existing dictionary values. */
  private static final Map<String, VerticalAlign> VALUES = new HashMap<>();
  /** Content should be aligned to top border of container. */
  public static final VerticalAlign TOP = VerticalAlign.create("top");
  /** Content should be aligned to middle of container. */
  public static final VerticalAlign MIDDLE = VerticalAlign.create("middle");
  /** Content should be aligned to bottom border of container. */
  public static final VerticalAlign BOTTOM = VerticalAlign.create("bottom");
  /** Content should be aligned to baseline of container. */
  public static final VerticalAlign BASELINE = VerticalAlign.create("baseline");
  /** Name of VerticalAlign element. */
  @NonNull private final String name;

  /**
   * Used to create new VerticalAlign element with specified name. Note that name will be converted
   * to lower case.
   *
   * @param name name of VerticalAlign element.
   * @return new VerticalAlign element (or existing one).
   */
  private static VerticalAlign create(@NonNull String name) {
    return VALUES.computeIfAbsent(name.toLowerCase(), VerticalAlign::new);
  }

  /**
   * Used to find VerticalAlign element with specified name. Note that name will be converted to
   * lower case.
   *
   * @param name name of VerticalAlign element.
   * @return existing VerticalAlign element or null.
   */
  public static VerticalAlign find(@NonNull String name) {
    return VALUES.get(name.toLowerCase());
  }

  /**
   * Returns set of all available VerticalAlign values.
   *
   * @return set of all available VerticalAlign values.
   */
  public static Set<VerticalAlign> values() {
    return Set.copyOf(VALUES.values());
  }

  /**
   * Returns true if there is a VerticalAlign value wth specified name.
   *
   * @param name VerticalAlign name.
   * @return true if there is a VerticalAlign value wth specified name.
   */
  public static boolean contains(@NonNull String name) {
    return VALUES.containsKey(name.toLowerCase());
  }

  @Override
  public String toString() {
    return name;
  }
}
