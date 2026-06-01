package com.spinyowl.spinygui.core.style.types;

import java.util.Arrays;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ScrollbarPart {
  SCROLLBAR("scrollbar", "::-webkit-scrollbar"),
  THUMB("-webkit-scrollbar-thumb", "::-webkit-scrollbar-thumb"),
  TRACK("-webkit-scrollbar-track", "::-webkit-scrollbar-track"),
  TRACK_PIECE("-webkit-scrollbar-track-piece", "::-webkit-scrollbar-track-piece"),
  BUTTON("-webkit-scrollbar-button", "::-webkit-scrollbar-button"),
  CORNER("-webkit-scrollbar-corner", "::-webkit-scrollbar-corner"),
  RESIZER("-webkit-scrollbar-resizer", "::-webkit-scrollbar-resizer");

  @NonNull private final String selectorName;
  @NonNull private final String canonicalSelector;

  public static ScrollbarPart fromSelectorName(String selectorName) {
    if ("-webkit-scrollbar".equals(selectorName)) {
      return SCROLLBAR;
    }
    return Arrays.stream(values())
        .filter(part -> part.selectorName.equals(selectorName))
        .findFirst()
        .orElse(null);
  }
}
