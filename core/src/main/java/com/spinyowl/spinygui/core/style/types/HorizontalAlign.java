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
public class HorizontalAlign {

  /** Stores all existing dictionary values. */
  private static final Map<String, HorizontalAlign> VALUES = new HashMap<>();
  /** Content should be aligned to left border of container. */
  public static final HorizontalAlign LEFT = HorizontalAlign.create("left");
  /** Content should be aligned to center of container. */
  public static final HorizontalAlign CENTER = HorizontalAlign.create("center");
  /** Content should be aligned to right border of container. */
  public static final HorizontalAlign RIGHT = HorizontalAlign.create("right");

  /** Name of HorizontalAlign element. */
  @NonNull private final String name;

  /**
   * Used to create new HorizontalAlign element with specified name. Note that name will be
   * converted to lower case.
   *
   * @param name name of HorizontalAlign element.
   * @return new HorizontalAlign element (or existing one).
   */
  private static HorizontalAlign create(@NonNull String name) {
    return VALUES.computeIfAbsent(name.toLowerCase(), HorizontalAlign::new);
  }

  /**
   * Used to find HorizontalAlign element with specified name. Note that name will be converted to
   * lower case.
   *
   * @param name name of HorizontalAlign element.
   * @return existing HorizontalAlign element or null.
   */
  public static HorizontalAlign find(@NonNull String name) {
    return VALUES.get(name.toLowerCase());
  }

  /**
   * Returns set of all available HorizontalAlign values.
   *
   * @return set of all available HorizontalAlign values.
   */
  public static Set<HorizontalAlign> values() {
    return Set.copyOf(VALUES.values());
  }

  /**
   * Returns true if there is a HorizontalAlign value wth specified name.
   *
   * @param name HorizontalAlign name.
   * @return true if there is a HorizontalAlign value wth specified name.
   */
  public static boolean contains(@NonNull String name) {
    return VALUES.containsKey(name.toLowerCase());
  }

  @Override
  public String toString() {
    return name;
  }
}
