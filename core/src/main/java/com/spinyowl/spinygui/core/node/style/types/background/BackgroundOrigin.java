package com.spinyowl.spinygui.core.node.style.types.background;

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
public class BackgroundOrigin {

  private static final Map<String, BackgroundOrigin> VALUES = new ConcurrentHashMap<>();

  /**
   * Default value. The background image starts from the upper left corner of the padding edge.
   */
  public static final BackgroundOrigin PADDING_BOX = BackgroundOrigin.create("padding-box");

  /**
   * The background image starts from the upper left corner of the border.
   */
  public static final BackgroundOrigin BORDER_BOX = BackgroundOrigin.create("border-box");

  /**
   * The background image starts from the upper left corner of the content.
   */
  public static final BackgroundOrigin CONTENT_BOX = BackgroundOrigin.create("content-box");

  /**
   * Name of position type (should be same as in css specification)
   */
  private final String name;

  /**
   * Creates {@link BackgroundOrigin} element with specified name.
   *
   * @param name name of {@link BackgroundOrigin} type (should be same as in css specification)
   */
  private BackgroundOrigin(String name) {
    this.name = name;
  }

  /**
   * Used to create new {@link BackgroundOrigin} element with specified name. Note that name will be
   * converted to lower case and it should be the same as names of css {@link BackgroundOrigin}
   * property in css specification.
   *
   * @param name name of {@link BackgroundOrigin} element.
   * @return new {@link BackgroundOrigin} element (or existing one).
   */
  public static BackgroundOrigin create(String name) {
    Objects.requireNonNull(name);
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
  public static BackgroundOrigin find(String name) {
    Objects.requireNonNull(name);
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
  public static boolean contains(String name) {
    if (name == null) {
      return false;
    }
    return values().stream().map(BackgroundOrigin::name)
        .anyMatch(v -> v.equalsIgnoreCase(name));
  }

}
