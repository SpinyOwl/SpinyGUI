package com.spinyowl.spinygui.core.style.types.flex;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Specifies the alignment between the lines inside a flexible container when the items do not use
 * all available space.
 */
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class AlignContent {

  private static final Map<String, AlignContent> VALUES = new ConcurrentHashMap<>();
  /** Default value. Lines stretch to take up the remaining space. */
  public static final AlignContent STRETCH = AlignContent.create("stretch");
  /** Lines are packed toward the center of the flex container. */
  public static final AlignContent CENTER = AlignContent.create("center");
  /** Lines are packed toward the start of the flex container. */
  public static final AlignContent FLEX_START = AlignContent.create("flex-start");
  /** Lines are packed toward the end of the flex container. */
  public static final AlignContent FLEX_END = AlignContent.create("flex-end");
  /** Lines are evenly distributed in the flex container. */
  public static final AlignContent SPACE_BETWEEN = AlignContent.create("space-between");
  /** Lines are evenly distributed in the flex container, with half-size spaces on either end. */
  public static final AlignContent SPACE_AROUND = AlignContent.create("space-around");

  @NonNull private final String name;

  private static AlignContent create(@NonNull String name) {
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
  public static AlignContent find(@NonNull String name) {
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
   * Returns true if there is a align-content value wth specified name.
   *
   * @param name align-content name.
   * @return true if there is a align-content value wth specified name.
   */
  public static boolean contains(@NonNull String name) {
    return values().stream().map(AlignContent::name).anyMatch(v -> v.equalsIgnoreCase(name));
  }
  @Override
  public String toString() {
    return name;
  }
}
