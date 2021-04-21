package com.spinyowl.spinygui.core.node.style.types;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class HorizontalAlign {

  /** Stores all existing dictionary values. */
  private static final Map<String, HorizontalAlign> VALUES = new ConcurrentHashMap<>();
  /** Content should be aligned to left border of container. */
  public static final HorizontalAlign LEFT = HorizontalAlign.create("left");
  /** Content should be aligned to center of container. */
  public static final HorizontalAlign CENTER = HorizontalAlign.create("center");
  /** Content should be aligned to right border of container. */
  public static final HorizontalAlign RIGHT = HorizontalAlign.create("right");

  /** Name of HorizontalAlign element. */
  private final String name;

  /**
   * Creates HorizontalAlign element with specified name.
   *
   * @param name name of HorizontalAlign type.
   */
  private HorizontalAlign(String name) {
    this.name = name;
  }

  /**
   * Used to create new HorizontalAlign element with specified name. Note that name will be
   * converted to lower case.
   *
   * @param name name of HorizontalAlign element.
   * @return new HorizontalAlign element (or existing one).
   */
  public static HorizontalAlign create(String name) {
    return VALUES.computeIfAbsent(Objects.requireNonNull(name).toLowerCase(), HorizontalAlign::new);
  }

  /**
   * Used to find HorizontalAlign element with specified name. Note that name will be converted to
   * lower case.
   *
   * @param name name of HorizontalAlign element.
   * @return existing HorizontalAlign element or null.
   */
  public static HorizontalAlign find(String name) {
    return VALUES.get(Objects.requireNonNull(name).toLowerCase());
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
  public static boolean contains(String name) {
    if (name == null) {
      return false;
    }
    return values().stream().map(HorizontalAlign::name).anyMatch(v -> v.equalsIgnoreCase(name));
  }
}
