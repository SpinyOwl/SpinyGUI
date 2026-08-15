package com.spinyowl.spinygui.core.node;

/** Shared UTF-16 boundary normalization for editable control indices. */
final class TextIndexNormalizer {
  private TextIndexNormalizer() {}

  static int clampAndSnapBackward(String value, int requestedIndex) {
    int index = Math.max(0, Math.min(requestedIndex, value.length()));
    if (index > 0
        && index < value.length()
        && Character.isHighSurrogate(value.charAt(index - 1))
        && Character.isLowSurrogate(value.charAt(index))) {
      return index - 1;
    }
    return index;
  }
}
