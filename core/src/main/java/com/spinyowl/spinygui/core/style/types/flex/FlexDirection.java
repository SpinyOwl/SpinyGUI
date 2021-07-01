package com.spinyowl.spinygui.core.style.types.flex;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/** Specifies the direction of the flexible items */
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class FlexDirection {

  private static final Map<String, FlexDirection> VALUES = new ConcurrentHashMap<>();

  /** Default value. The flexible items are displayed horizontally, as a row. */
  public static final FlexDirection ROW = FlexDirection.create("row");
  /** Same as row, but in reverse order. */
  public static final FlexDirection ROW_REVERSE = FlexDirection.create("row-reverse");
  /** The flexible items are displayed vertically, as a column. */
  public static final FlexDirection COLUMN = FlexDirection.create("column");
  /** Same as column, but in reverse order. */
  public static final FlexDirection COLUMN_REVERSE = FlexDirection.create("column-reverse");

  @NonNull private final String name;

  private static FlexDirection create(@NonNull String name) {
    return VALUES.computeIfAbsent(name.toLowerCase(), FlexDirection::new);
  }

  /**
   * Used to find flex-direction element with specified name. Note that name will be converted to
   * lower case and it should be the same as names of css flex-direction property in css
   * specification.
   *
   * @param name name of flex-direction element.
   * @return existing align-content element or null.
   */
  public static FlexDirection find(@NonNull String name) {
    return VALUES.get(name.toLowerCase());
  }

  /**
   * Returns set of all available flex-direction values.
   *
   * @return set of all available flex-direction values.
   */
  public static Set<FlexDirection> values() {
    return Set.copyOf(VALUES.values());
  }

  /**
   * Returns true if there is a flex-direction value wth specified name.
   *
   * @param name flex-direction name.
   * @return true if there is a flex-direction value wth specified name.
   */
  public static boolean contains(@NonNull String name) {
    return values().stream().map(FlexDirection::name).anyMatch(v -> v.equalsIgnoreCase(name));
  }
  @Override
  public String toString() {
    return name;
  }
}
