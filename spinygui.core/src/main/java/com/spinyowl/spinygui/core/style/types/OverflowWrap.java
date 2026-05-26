package com.spinyowl.spinygui.core.style.types;

import static lombok.AccessLevel.PRIVATE;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

/** CSS overflow-wrap. */
@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = PRIVATE)
public final class OverflowWrap {

  private static final Map<String, OverflowWrap> VALUES = new HashMap<>();

  public static final OverflowWrap NORMAL = OverflowWrap.create("normal");
  public static final OverflowWrap BREAK_WORD = OverflowWrap.create("break-word");
  public static final OverflowWrap ANYWHERE = OverflowWrap.create("anywhere");

  @NonNull private final String name;

  public static OverflowWrap create(@NonNull String name) {
    return VALUES.computeIfAbsent(name.toLowerCase(), OverflowWrap::new);
  }

  public static OverflowWrap find(@NonNull String name) {
    return VALUES.get(name.toLowerCase());
  }

  public static Set<OverflowWrap> values() {
    return Set.copyOf(VALUES.values());
  }

  public static boolean contains(@NonNull String name) {
    return VALUES.containsKey(name.toLowerCase());
  }

  @Override
  public String toString() {
    return name;
  }
}
