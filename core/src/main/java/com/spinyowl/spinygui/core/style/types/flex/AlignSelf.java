package com.spinyowl.spinygui.core.style.types.flex;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/** Specifies the alignment for selected items inside a flexible container. */
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class AlignSelf {

  private static final Map<String, AlignSelf> VALUES = new ConcurrentHashMap<>();

  /**
   * Default. The element inherits its parent container's align-items property, or "stretch" if it
   * has no parent container.
   */
  public static final AlignSelf AUTO = AlignSelf.create("auto");
  /** The element is positioned to fit the container. */
  public static final AlignSelf STRETCH = AlignSelf.create("stretch");
  /** The element is positioned at the center of the container. */
  public static final AlignSelf CENTER = AlignSelf.create("center");
  /** The element is positioned at the beginning of the container. */
  public static final AlignSelf FLEX_START = AlignSelf.create("flex-start");
  /** The element is positioned at the end of the container. */
  public static final AlignSelf FLEX_END = AlignSelf.create("flex-end");
  /** The element is positioned at the baseline of the container. */
  public static final AlignSelf BASELINE = AlignSelf.create("baseline");

  @NonNull private final String name;

  private static AlignSelf create(@NonNull String name) {
    return VALUES.computeIfAbsent(name.toLowerCase(), AlignSelf::new);
  }

  /**
   * Used to find align-self element with specified name. Note that name will be converted to lower
   * case and it should be the same as names of css align-self property in css specification.
   *
   * @param name name of align-self element.
   * @return existing align-content element or null.
   */
  public static AlignSelf find(@NonNull String name) {
    return VALUES.get(name.toLowerCase());
  }

  /**
   * Returns set of all available align-self values.
   *
   * @return set of all available align-self values.
   */
  public static Set<AlignSelf> values() {
    return Set.copyOf(VALUES.values());
  }

  /**
   * Returns true if there is a align-self value wth specified name.
   *
   * @param name align-self name.
   * @return true if there is a align-self value wth specified name.
   */
  public static boolean contains(@NonNull String name) {
    return values().stream().map(AlignSelf::name).anyMatch(v -> v.equalsIgnoreCase(name));
  }
  @Override
  public String toString() {
    return name;
  }
}
