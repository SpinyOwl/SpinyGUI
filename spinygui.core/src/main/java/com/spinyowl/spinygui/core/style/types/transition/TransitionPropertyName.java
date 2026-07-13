package com.spinyowl.spinygui.core.style.types.transition;

import java.util.Arrays;
import java.util.Optional;

/** M3's explicitly supported transitionable CSS property names. */
public enum TransitionPropertyName {
  OPACITY("opacity"), COLOR("color"), BACKGROUND_COLOR("background-color"),
  BORDER_TOP_COLOR("border-top-color"), BORDER_RIGHT_COLOR("border-right-color"),
  BORDER_BOTTOM_COLOR("border-bottom-color"), BORDER_LEFT_COLOR("border-left-color"),
  TRANSFORM("transform");

  private final String cssName;
  TransitionPropertyName(String cssName) { this.cssName = cssName; }
  public String cssName() { return cssName; }
  public static Optional<TransitionPropertyName> fromCssName(String value) {
    return Arrays.stream(values()).filter(property -> property.cssName.equalsIgnoreCase(value)).findFirst();
  }
}
