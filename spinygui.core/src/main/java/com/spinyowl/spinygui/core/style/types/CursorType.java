package com.spinyowl.spinygui.core.style.types;

import java.util.Locale;
import java.util.Set;

/** Supported CSS cursor keywords, including {@link #AUTO} as the unstylized default. */
public enum CursorType {
  AUTO,
  DEFAULT,
  POINTER,
  TEXT,
  MOVE,
  EW_RESIZE,
  NS_RESIZE,
  NWSE_RESIZE,
  NESW_RESIZE;

  /** Parses a CSS keyword or returns {@code null} for an unsupported keyword. */
  public static CursorType find(String value) {
    if (value == null) return null;
    try {
      return valueOf(value.replace('-', '_').toUpperCase(Locale.ROOT));
    } catch (IllegalArgumentException ignored) {
      return null;
    }
  }

  /** Returns whether the CSS keyword is supported. */
  public static boolean contains(String value) {
    return find(value) != null;
  }

  /** Returns the CSS spelling of this cursor keyword. */
  public String cssName() {
    return name().toLowerCase(Locale.ROOT).replace('_', '-');
  }

  /** Returns the authored cursor values accepted by the first support slice. */
  public static Set<CursorType> valuesForCss() {
    return Set.of(AUTO, DEFAULT, POINTER, TEXT, MOVE, EW_RESIZE, NS_RESIZE, NWSE_RESIZE, NESW_RESIZE);
  }
}
