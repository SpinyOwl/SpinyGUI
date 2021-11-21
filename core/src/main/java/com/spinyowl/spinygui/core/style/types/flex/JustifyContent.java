package com.spinyowl.spinygui.core.style.types.flex;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Specifies the alignment between the items inside a flexible container when the items do not use
 * all available space.
 */
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class JustifyContent {

  private static final Map<String, JustifyContent> VALUES = new HashMap<>();
  /** Default value. Items are positioned at the beginning of the container. */
  public static final JustifyContent FLEX_START = JustifyContent.create("flex-start");
  /** Items are positioned at the end of the container. */
  public static final JustifyContent FLEX_END = JustifyContent.create("flex-end");
  /** Items are positioned at the center of the container. */
  public static final JustifyContent CENTER = JustifyContent.create("center");
  /** Items are positioned with space between the lines. */
  public static final JustifyContent SPACE_BETWEEN = JustifyContent.create("space-between");
  /** Items are positioned with space before, between, and after the lines. */
  public static final JustifyContent SPACE_AROUND = JustifyContent.create("space-around");
  /** Distribute items evenly. Items have equal space around them. */
  public static final JustifyContent SPACE_EVENLY = JustifyContent.create("space-evenly");

  @NonNull private final String name;

  private static JustifyContent create(@NonNull String name) {
    return VALUES.computeIfAbsent(name.toLowerCase(), JustifyContent::new);
  }

  /**
   * Used to find justify-content element with specified name. Note that name will be converted to
   * lower case and it should be the same as names of css justify-content property in css
   * specification.
   *
   * @param name name of justify-content element.
   * @return existing align-content element or null.
   */
  public static JustifyContent find(@NonNull String name) {
    return VALUES.get(name.toLowerCase());
  }

  /**
   * Returns set of all available justify-content values.
   *
   * @return set of all available justify-content values.
   */
  public static Set<JustifyContent> values() {
    return Set.copyOf(VALUES.values());
  }

  /**
   * Returns true if there is a justify-content value wth specified name.
   *
   * @param name justify-content name.
   * @return true if there is a justify-content value wth specified name.
   */
  public static boolean contains(@NonNull String name) {
    return VALUES.containsKey(name.toLowerCase());
  }

  @Override
  public String toString() {
    return name;
  }
}
