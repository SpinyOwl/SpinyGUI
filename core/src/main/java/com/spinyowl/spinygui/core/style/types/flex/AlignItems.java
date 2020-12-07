package com.spinyowl.spinygui.core.style.types.flex;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Specifies the alignment for items inside a flexible container.
 */
public final class AlignItems {

  private static final Map<String, AlignItems> VALUES = new ConcurrentHashMap<>();
  /**
   * Auto.
   */
  public static final AlignItems AUTO = AlignItems.create("auto");
  /**
   * Default. Items are stretched to fit the container.
   */
  public static final AlignItems STRETCH = AlignItems.create("stretch");
  /**
   * Items are positioned at the center of the container.
   */
  public static final AlignItems CENTER = AlignItems.create("center");
  /**
   * Items are positioned at the beginning of the container.
   */
  public static final AlignItems FLEX_START = AlignItems.create("flex-start");
  /**
   * Items are positioned at the end of the container.
   */
  public static final AlignItems FLEX_END = AlignItems.create("flex-end");
  /**
   * Items are positioned at the baseline of the container.
   */
  public static final AlignItems BASELINE = AlignItems.create("baseline");

  private final String name;

  private AlignItems(String name) {
    this.name = name;
  }

  private static AlignItems create(String name) {
    Objects.requireNonNull(name);
    return VALUES.computeIfAbsent(name.toLowerCase(), AlignItems::new);
  }

  /**
   * Used to find align-items element with specified name. Note that name will be converted to lower
   * case and it should be the same as names of css align-items property in css specification.
   *
   * @param name name of align-items element.
   * @return existing align-content element or null.
   */
  public static AlignItems find(String name) {
    Objects.requireNonNull(name);
    return VALUES.get(name.toLowerCase());
  }

  /**
   * Returns set of all available align-items values.
   *
   * @return set of all available align-items values.
   */
  public static Set<AlignItems> values() {
    return Set.copyOf(VALUES.values());
  }

  /**
   * Returns true if there is a align-items value wth specified name.
   *
   * @param name align-items name.
   * @return true if there is a align-items value wth specified name.
   */
  public static boolean contains(String name) {
    if (name == null) {
      return false;
    }
    return values().stream().map(AlignItems::getName)
        .anyMatch(v -> v.equalsIgnoreCase(name));
  }

  /**
   * Name of align-items.
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
    AlignItems alignItems = (AlignItems) o;
    return Objects.equals(name, alignItems.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", AlignItems.class.getSimpleName() + "[", "]")
        .add("name='" + name + "'")
        .toString();
  }
}
