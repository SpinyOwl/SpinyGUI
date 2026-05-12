package com.spinyowl.spinygui.core.style.types;

import static lombok.AccessLevel.PRIVATE;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

/** CSS text-align. */
@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = PRIVATE)
public final class TextAlign {

  private static final Map<String, TextAlign> VALUES = new HashMap<>();

  public static final TextAlign LEFT = TextAlign.create("left");
  public static final TextAlign RIGHT = TextAlign.create("right");
  public static final TextAlign CENTER = TextAlign.create("center");
  public static final TextAlign JUSTIFY = TextAlign.create("justify");

  @NonNull private final String name;

  public static TextAlign create(@NonNull String name) {
    return VALUES.computeIfAbsent(name.toLowerCase(), TextAlign::new);
  }

  public static TextAlign find(@NonNull String name) {
    return VALUES.get(name.toLowerCase());
  }

  public static Set<TextAlign> values() {
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
