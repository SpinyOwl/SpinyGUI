package com.spinyowl.spinygui.core.style.types.flex;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Specifies the direction of the flexible items
 */
public final class FlexDirection {

  private static final Map<String, FlexDirection> VALUES = new ConcurrentHashMap<>();

  /**
   * Default value. The flexible items are displayed horizontally, as a row.
   */
  public static final FlexDirection ROW = FlexDirection.create("row");
  /**
   * Same as row, but in reverse order.
   */
  public static final FlexDirection ROW_REVERSE = FlexDirection.create("row-reverse");
  /**
   * The flexible items are displayed vertically, as a column.
   */
  public static final FlexDirection COLUMN = FlexDirection.create("column");
  /**
   * Same as column, but in reverse order.
   */
  public static final FlexDirection COLUMN_REVERSE = FlexDirection.create("column-reverse");

  private String name;

  private FlexDirection(String name) {
    this.name = name;
  }

  private static FlexDirection create(String name) {
    Objects.requireNonNull(name);
    return VALUES.computeIfAbsent(name.toLowerCase(), FlexDirection::new);
  }

  /**
   * Used to find flex-direction element with specified name. Note that name will be converted to
   * lower case and it should be the same as names of css flex-direction property in css
   * specification.
   *
   * @param name name of flex-direction element.
   * @return existing align-content element or null.
   */
  public static FlexDirection find(String name) {
    Objects.requireNonNull(name);
    return VALUES.get(name.toLowerCase());
  }

  /**
   * Returns set of all available flex-direction values.
   *
   * @return set of all available flex-direction values.
   */
  public static Set<FlexDirection> values() {
    return Set.copyOf(VALUES.values());
  }

  /**
   * Returns true there is a flex-direction value wth specified name.
   *
   * @param name flex-direction name.
   * @return true there is a flex-direction value wth specified name.
   */
  public static boolean contains(String name) {
    if (name == null) {
      return false;
    }
    return values().stream().map(FlexDirection::getName)
        .anyMatch(v -> v.equalsIgnoreCase(name));
  }

  /**
   * Name of flex-direction.
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
    FlexDirection flexDirection = (FlexDirection) o;
    return Objects.equals(name, flexDirection.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", FlexDirection.class.getSimpleName() + "[", "]")
        .add("name='" + name + "'")
        .toString();
  }

}
