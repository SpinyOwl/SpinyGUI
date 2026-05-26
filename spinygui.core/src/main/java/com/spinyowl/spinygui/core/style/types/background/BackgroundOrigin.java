package com.spinyowl.spinygui.core.style.types.background;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class BackgroundOrigin {

  private static final Map<String, BackgroundOrigin> VALUES = new HashMap<>();

  /** Default value. The background image starts from the upper left corner of the padding edge. */
  public static final BackgroundOrigin PADDING_BOX = BackgroundOrigin.create("padding-box");

  /** The background image starts from the upper left corner of the border. */
  public static final BackgroundOrigin BORDER_BOX = BackgroundOrigin.create("border-box");

  /** The background image starts from the upper left corner of the content. */
  public static final BackgroundOrigin CONTENT_BOX = BackgroundOrigin.create("content-box");

  /** Name of position type (should be same as in css specification) */
  @NonNull private final String name;

  /**
   * Used to create new {@link BackgroundOrigin} element with specified name. Note that name will be
   * converted to lower case and it should be the same as names of css {@link BackgroundOrigin}
   * property in css specification.
   *
   * @param name name of {@link BackgroundOrigin} element.
   * @return new {@link BackgroundOrigin} element (or existing one).
   */
  public static BackgroundOrigin create(@NonNull String name) {
    return VALUES.computeIfAbsent(name.toLowerCase(), BackgroundOrigin::new);
  }

  /**
   * Used to find {@link BackgroundOrigin} element with specified name. Note that name will be
   * converted to lower case and it should be the same as names of css {@link BackgroundOrigin}
   * property in css specification.
   *
   * @param name name of {@link BackgroundOrigin} element.
   * @return existing {@link BackgroundOrigin} element or null.
   */
  public static BackgroundOrigin find(@NonNull String name) {
    return VALUES.get(name.toLowerCase());
  }

  /**
   * Returns set of all available {@link BackgroundOrigin} values.
   *
   * @return set of all available {@link BackgroundOrigin} values.
   */
  public static Set<BackgroundOrigin> values() {
    return Set.copyOf(VALUES.values());
  }

  /**
   * Returns true if there is a BackgroundOrigin value wth specified name.
   *
   * @param name {@link BackgroundOrigin} name.
   * @return true if there is a BackgroundOrigin value wth specified name.
   */
  public static boolean contains(@NonNull String name) {
    return VALUES.containsKey(name.toLowerCase());
  }

  @Override
  public String toString() {
    return name;
  }
}
