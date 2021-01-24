package com.spinyowl.spinygui.core.node.style.types.flex;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Specifies whether the flexible items should wrap or not.
 */
public final class FlexWrap {

  private static final Map<String, FlexWrap> VALUES = new ConcurrentHashMap<>();

  /**
   * Default value. Specifies that the flexible items will not wrap.
   */
  public static final FlexWrap NOWRAP = FlexWrap.create("nowrap");
  /**
   * Specifies that the flexible items will wrap if necessary.
   */
  public static final FlexWrap WRAP = FlexWrap.create("wrap");
  /**
   * Specifies that the flexible items will wrap, if necessary, in reverse order.
   */
  public static final FlexWrap WRAP_REVERSE = FlexWrap.create("wrap-reverse");

  private final String name;

  private FlexWrap(String name) {
    this.name = name;
  }

  private static FlexWrap create(String name) {
    Objects.requireNonNull(name);
    return VALUES.computeIfAbsent(name.toLowerCase(), FlexWrap::new);
  }

  /**
   * Used to find flex-wrap element with specified name. Note that name will be converted to lower
   * case and it should be the same as names of css flex-wrap property in css specification.
   *
   * @param name name of flex-wrap element.
   * @return existing align-content element or null.
   */
  public static FlexWrap find(String name) {
    Objects.requireNonNull(name);
    return VALUES.get(name.toLowerCase());
  }

  /**
   * Returns set of all available flex-wrap values.
   *
   * @return set of all available flex-wrap values.
   */
  public static Set<FlexWrap> values() {
    return Set.copyOf(VALUES.values());
  }

  /**
   * Returns true if there is a flex-wrap value wth specified name.
   *
   * @param name flex-wrap name.
   * @return true if there is a flex-wrap value wth specified name.
   */
  public static boolean contains(String name) {
    if (name == null) {
      return false;
    }
    return values().stream().map(FlexWrap::getName)
        .anyMatch(v -> v.equalsIgnoreCase(name));
  }

  /**
   * Name of flex-wrap.
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
    FlexWrap flexWrap = (FlexWrap) o;
    return Objects.equals(name, flexWrap.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", FlexWrap.class.getSimpleName() + "[", "]")
        .add("name='" + name + "'")
        .toString();
  }

}
