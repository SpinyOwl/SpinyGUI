package com.spinyowl.spinygui.core.style.types.flex;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/** Specifies whether the flexible items should wrap or not. */
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class FlexWrap {

  private static final Map<String, FlexWrap> VALUES = new ConcurrentHashMap<>();

  /** Default value. Specifies that the flexible items will not wrap. */
  public static final FlexWrap NOWRAP = FlexWrap.create("nowrap");
  /** Specifies that the flexible items will wrap if necessary. */
  public static final FlexWrap WRAP = FlexWrap.create("wrap");
  /** Specifies that the flexible items will wrap, if necessary, in reverse order. */
  public static final FlexWrap WRAP_REVERSE = FlexWrap.create("wrap-reverse");

  @NonNull private final String name;

  private static FlexWrap create(@NonNull String name) {
    return VALUES.computeIfAbsent(name.toLowerCase(), FlexWrap::new);
  }

  /**
   * Used to find flex-wrap element with specified name. Note that name will be converted to lower
   * case and it should be the same as names of css flex-wrap property in css specification.
   *
   * @param name name of flex-wrap element.
   * @return existing align-content element or null.
   */
  public static FlexWrap find(@NonNull String name) {
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
  public static boolean contains(@NonNull String name) {
    return values().stream().map(FlexWrap::name).anyMatch(v -> v.equalsIgnoreCase(name));
  }
  @Override
  public String toString() {
    return name;
  }
}
