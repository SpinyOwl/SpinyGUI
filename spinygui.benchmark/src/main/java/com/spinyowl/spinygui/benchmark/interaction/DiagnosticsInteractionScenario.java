package com.spinyowl.spinygui.benchmark.interaction;

import java.util.List;

/** Stable scenario vocabulary for the E6/M1.6 diagnostics-panel evidence fixture. */
public enum DiagnosticsInteractionScenario {
  STATIONARY_POINTER("stationary-pointer"),
  POINTER_MOVE_WITHIN_TEXT_NODE("pointer-move-within-text-node"),
  POINTER_CROSS_TEXT_BOUNDARY("pointer-cross-text-boundary"),
  PAINT_ONLY_HOVER("paint-only-hover"),
  DIMENSION_AFFECTING_HOVER("dimension-affecting-hover"),
  KEYBOARD_ONLY_INPUT("keyboard-only-input"),
  SCROLL("scroll"),
  CLICK_FOCUS("click-focus"),
  TEXT_EDITING("text-editing"),
  RESIZE("resize"),
  UNKNOWN_LISTENER_EFFECT("unknown-listener-effect");

  public static final List<String> REQUIRED_NAMES =
      List.of(values()).stream().map(DiagnosticsInteractionScenario::id).toList();

  private final String id;

  DiagnosticsInteractionScenario(String id) {
    this.id = id;
  }

  public String id() {
    return id;
  }
}
