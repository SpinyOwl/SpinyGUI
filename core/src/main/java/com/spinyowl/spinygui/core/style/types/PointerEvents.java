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
public class PointerEvents {

  /** Stores all existing dictionary values. */
  private static final Map<String, PointerEvents> VALUES = new ConcurrentHashMap<>();

  /** The element reacts to pointer events, like :hover and click. This is default */
  public static final PointerEvents AUTO = PointerEvents.create("auto");
  /** The element does not react to pointer events */
  public static final PointerEvents NONE = PointerEvents.create("none");

  /** Name of PointerEvents element. */
  private final String name;

  /**
   * Creates PointerEvents element with specified name.
   *
   * @param name name of PointerEvents type.
   */
  private PointerEvents(String name) {
    this.name = name;
  }

  /**
   * Used to create new PointerEvents element with specified name. Note that name will be converted
   * to lower case.
   *
   * @param name name of PointerEvents element.
   * @return new PointerEvents element (or existing one).
   */
  public static PointerEvents create(String name) {
    return VALUES.computeIfAbsent(Objects.requireNonNull(name).toLowerCase(), PointerEvents::new);
  }

  /**
   * Used to find PointerEvents element with specified name. Note that name will be converted to
   * lower case.
   *
   * @param name name of PointerEvents element.
   * @return existing PointerEvents element or null.
   */
  public static PointerEvents find(String name) {
    return VALUES.get(Objects.requireNonNull(name).toLowerCase());
  }

  /**
   * Returns set of all available PointerEvents values.
   *
   * @return set of all available PointerEvents values.
   */
  public static Set<PointerEvents> values() {
    return Set.copyOf(VALUES.values());
  }

  /**
   * Returns true if there is a PointerEvents value wth specified name.
   *
   * @param name PointerEvents name.
   * @return true if there is a PointerEvents value wth specified name.
   */
  public static boolean contains(String name) {
    if (name == null) {
      return false;
    }
    return values().stream().map(PointerEvents::name).anyMatch(v -> v.equalsIgnoreCase(name));
  }
  @Override
  public String toString() {
    return name;
  }
}
