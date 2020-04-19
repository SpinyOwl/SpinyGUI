package com.spinyowl.spinygui.core.style.types.flex;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Specifies the alignment between the lines inside a flexible container when the items do not use
 * all available space.
 */
public final class AlignContent {

  private static final Map<String, AlignContent> VALUES = new ConcurrentHashMap<>();
  /**
   * Default value. Lines stretch to take up the remaining space.
   */
  public static final AlignContent STRETCH = AlignContent.create("stretch");
  /**
   * Lines are packed toward the center of the flex container.
   */
  public static final AlignContent CENTER = AlignContent.create("center");
  /**
   * Lines are packed toward the start of the flex container.
   */
  public static final AlignContent FLEX_START = AlignContent.create("flex-start");
  /**
   * Lines are packed toward the end of the flex container.
   */
  public static final AlignContent FLEX_END = AlignContent.create("flex-end");
  /**
   * Lines are evenly distributed in the flex container.
   */
  public static final AlignContent SPACE_BETWEEN = AlignContent.create("space-between");
  /**
   * Lines are evenly distributed in the flex container, with half-size spaces on either end.
   */
  public static final AlignContent SPACE_AROUND = AlignContent.create("space-around");

  private String name;

  private AlignContent(String name) {
    this.name = name;
  }

  private static AlignContent create(String name) {
    Objects.requireNonNull(name);
    return VALUES.computeIfAbsent(name.toLowerCase(), AlignContent::new);
  }

  /**
   * Used to find align-content element with specified name. Note that name will be converted to
   * lower case and it should be the same as names of css align-content property in css
   * specification.
   *
   * @param name name of align-content element.
   * @return existing align-content element or null.
   */
  public static AlignContent find(String name) {
    Objects.requireNonNull(name);
    return VALUES.get(name.toLowerCase());
  }

  /**
   * Returns set of all available align-content values.
   *
   * @return set of all available align-content values.
   */
  public static Set<AlignContent> values() {
    return Set.copyOf(VALUES.values());
  }

  /**
   * Returns true there is a align-content value wth specified name.
   *
   * @param name align-content name.
   * @return true there is a align-content value wth specified name.
   */
  public static boolean contains(String name) {
    if (name == null) {
      return false;
    }
    return values().stream().map(AlignContent::getName)
      .anyMatch(v -> v.equalsIgnoreCase(name));
  }

  /**
   * Name of align-content.
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
    AlignContent alignContent = (AlignContent) o;
    return Objects.equals(name, alignContent.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", AlignContent.class.getSimpleName() + "[", "]")
      .add("name='" + name + "'")
      .toString();
  }
}
