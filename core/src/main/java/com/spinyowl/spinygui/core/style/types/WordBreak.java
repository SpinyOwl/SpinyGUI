package com.spinyowl.spinygui.core.style.types;

import static lombok.AccessLevel.PRIVATE;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

/** CSS word-break. */
@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = PRIVATE)
public final class WordBreak {

  private static final Map<String, WordBreak> VALUES = new HashMap<>();

  public static final WordBreak NORMAL = WordBreak.create("normal");
  public static final WordBreak BREAK_ALL = WordBreak.create("break-all");
  public static final WordBreak KEEP_ALL = WordBreak.create("keep-all");
  public static final WordBreak BREAK_WORD = WordBreak.create("break-word");

  @NonNull private final String name;

  public static WordBreak create(@NonNull String name) {
    return VALUES.computeIfAbsent(name.toLowerCase(), WordBreak::new);
  }

  public static WordBreak find(@NonNull String name) {
    return VALUES.get(name.toLowerCase());
  }

  public static Set<WordBreak> values() {
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
