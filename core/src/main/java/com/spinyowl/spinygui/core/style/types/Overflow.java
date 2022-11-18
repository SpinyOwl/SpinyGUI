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
public final class Overflow {

  private static final Map<String, Overflow> VALUES = new HashMap<>();

  /**
   * The content is not clipped, and it may be rendered outside the left and right edges. This is
   * default.
   */
  public static final Overflow VISIBLE = Overflow.create("visible");

  /**
   * The content is clipped - and no scrolling mechanism is provided. No scrollbars provided,
   * content can be scrolled only programmatically.
   */
  public static final Overflow HIDDEN = Overflow.create("hidden");

  /** The content is clipped and a scrolling mechanism is provided. Scrollbars always appear. */
  public static final Overflow SCROLL = Overflow.create("scroll");

  /**
   * The content is clipped and a scrolling mechanism is provided. Scrollbars appear only when
   * necessary.
   */
  public static final Overflow AUTO = Overflow.create("auto");

  /**
   * The content is clipped and a scrolling mechanism is provided. Scrollbars appear only when
   * necessary. Behaves the same as auto, but with the scrollbars drawn on top of content instead of
   * taking up space.
   */
  public static final Overflow OVERLAY = Overflow.create("overlay");

  /** Name of Overflow */
  @NonNull private final String name;

  /**
   * Used to create new Overflow element with specified name. Note that name will be converted to
   * lower case.
   *
   * @param name name of Overflow element.
   * @return new Overflow element (or existing one).
   */
  public static Overflow create(@NonNull String name) {
    return VALUES.computeIfAbsent(name.toLowerCase(), Overflow::new);
  }

  /**
   * Used to find Overflow element with specified name. Note that name will be converted to lower
   * case.
   *
   * @param name name of Overflow element.
   * @return existing Overflow element or null.
   */
  public static Overflow find(@NonNull String name) {
    return VALUES.get(name.toLowerCase());
  }

  /**
   * Returns set of all available Overflow values.
   *
   * @return set of all available Overflow values.
   */
  public static Set<Overflow> values() {
    return Set.copyOf(VALUES.values());
  }

  /**
   * Returns true if there is a Overflow value wth specified name.
   *
   * @param name Overflow name.
   * @return true if there is a Overflow value wth specified name.
   */
  public static boolean contains(@NonNull String name) {
    return VALUES.containsKey(name.toLowerCase());
  }

  @Override
  public String toString() {
    return name;
  }
}
