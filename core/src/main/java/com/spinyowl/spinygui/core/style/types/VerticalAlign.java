package com.spinyowl.spinygui.core.style.types;

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
public class VerticalAlign {

  /**
   * Stores all existing dictionary values.
   */
  private static final Map<String, VerticalAlign> VALUES = new ConcurrentHashMap<>();
  /**
   * Content should be aligned to top border of container.
   */
  public static final VerticalAlign TOP = VerticalAlign.create("top");
  /**
   * Content should be aligned to middle of container.
   */
  public static final VerticalAlign MIDDLE = VerticalAlign.create("middle");
  /**
   * Content should be aligned to bottom border of container.
   */
  public static final VerticalAlign BOTTOM = VerticalAlign.create("bottom");
  /**
   * Content should be aligned to baseline of container.
   */
  public static final VerticalAlign BASELINE = VerticalAlign.create("baseline");
  /**
   * Name of VerticalAlign element.
   */
  private final String name;

  /**
   * Creates VerticalAlign element with specified name.
   *
   * @param name name of VerticalAlign type.
   */
  private VerticalAlign(String name) {
    this.name = name;
  }

  /**
   * Used to create new VerticalAlign element with specified name. Note that name will be converted
   * to lower case.
   *
   * @param name name of VerticalAlign element.
   * @return new VerticalAlign element (or existing one).
   */
  public static VerticalAlign create(String name) {
    return VALUES.computeIfAbsent(Objects.requireNonNull(name).toLowerCase(), VerticalAlign::new);
  }

  /**
   * Used to find VerticalAlign element with specified name. Note that name will be converted to
   * lower case.
   *
   * @param name name of VerticalAlign element.
   * @return existing VerticalAlign element or null.
   */
  public static VerticalAlign find(String name) {
    return VALUES.get(Objects.requireNonNull(name).toLowerCase());
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
  public static boolean contains(String name) {
    if (name == null) {
      return false;
    }
    return values().stream().map(VerticalAlign::name).anyMatch(v -> v.equalsIgnoreCase(name));
  }

}
