package com.spinyowl.spinygui.core.style.types;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@Getter
@EqualsAndHashCode
public class PointerEvents {

  /** Stores all existing dictionary values. */
  private static final Map<String, PointerEvents> VALUES = new HashMap<>();

  /** The element reacts to pointer events, like :hover and click. This is default */
  public static final PointerEvents AUTO = PointerEvents.create("auto");
  /** The element does not react to pointer events */
  public static final PointerEvents NONE = PointerEvents.create("none");

  /** Name of PointerEvents element. */
  @NonNull private final String name;

  /**
   * Creates PointerEvents element with specified name.
   *
   * @param name name of PointerEvents type.
   */
  private PointerEvents(@NonNull String name) {
    this.name = name;
  }

  /**
   * Used to create new PointerEvents element with specified name. Note that name will be converted
   * to lower case.
   *
   * @param name name of PointerEvents element.
   * @return new PointerEvents element (or existing one).
   */
  public static PointerEvents create(@NonNull String name) {
    return VALUES.computeIfAbsent(Objects.requireNonNull(name).toLowerCase(), PointerEvents::new);
  }

  /**
   * Used to find PointerEvents element with specified name. Note that name will be converted to
   * lower case.
   *
   * @param name name of PointerEvents element.
   * @return existing PointerEvents element or null.
   */
  public static PointerEvents find(@NonNull String name) {
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
  public static boolean contains(@NonNull String name) {
    return VALUES.containsKey(name.toLowerCase());
  }

  @Override
  public String toString() {
    return name;
  }
}
