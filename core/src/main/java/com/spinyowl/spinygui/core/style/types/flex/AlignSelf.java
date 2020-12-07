package com.spinyowl.spinygui.core.style.types.flex;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Specifies the alignment for selected items inside a flexible container.
 */
public final class AlignSelf {

  private static final Map<String, AlignSelf> VALUES = new ConcurrentHashMap<>();

  /**
   * Default. The element inherits its parent container's align-items property, or "stretch" if it
   * has no parent container.
   */
  public static final AlignSelf AUTO = AlignSelf.create("auto");
  /**
   * The element is positioned to fit the container.
   */
  public static final AlignSelf STRETCH = AlignSelf.create("stretch");
  /**
   * The element is positioned at the center of the container.
   */
  public static final AlignSelf CENTER = AlignSelf.create("center");
  /**
   * The element is positioned at the beginning of the container.
   */
  public static final AlignSelf FLEX_START = AlignSelf.create("flex-start");
  /**
   * The element is positioned at the end of the container.
   */
  public static final AlignSelf FLEX_END = AlignSelf.create("flex-end");
  /**
   * The element is positioned at the baseline of the container.
   */
  public static final AlignSelf BASELINE = AlignSelf.create("baseline");

  private final String name;

  private AlignSelf(String name) {
    this.name = name;
  }

  private static AlignSelf create(String name) {
    Objects.requireNonNull(name);
    return VALUES.computeIfAbsent(name.toLowerCase(), AlignSelf::new);
  }

  /**
   * Used to find align-self element with specified name. Note that name will be converted to lower
   * case and it should be the same as names of css align-self property in css specification.
   *
   * @param name name of align-self element.
   * @return existing align-content element or null.
   */
  public static AlignSelf find(String name) {
    Objects.requireNonNull(name);
    return VALUES.get(name.toLowerCase());
  }

  /**
   * Returns set of all available align-self values.
   *
   * @return set of all available align-self values.
   */
  public static Set<AlignSelf> values() {
    return Set.copyOf(VALUES.values());
  }

  /**
   * Returns true if there is a align-self value wth specified name.
   *
   * @param name align-self name.
   * @return true if there is a align-self value wth specified name.
   */
  public static boolean contains(String name) {
    if (name == null) {
      return false;
    }
    return values().stream().map(AlignSelf::getName)
        .anyMatch(v -> v.equalsIgnoreCase(name));
  }

  /**
   * Name of align-self.
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
    AlignSelf alignSelf = (AlignSelf) o;
    return Objects.equals(name, alignSelf.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", AlignSelf.class.getSimpleName() + "[", "]")
        .add("name='" + name + "'")
        .toString();
  }
}
