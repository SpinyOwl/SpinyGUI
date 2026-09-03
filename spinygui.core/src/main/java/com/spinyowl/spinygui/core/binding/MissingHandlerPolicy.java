package com.spinyowl.spinygui.core.binding;

/** Policy applied when an XML event declaration cannot resolve an available named handler. */
public enum MissingHandlerPolicy {
  /** Fail event dispatch with an actionable resolution error. */
  ERROR,
  /** Skip the unresolved handler and report a structured diagnostic. */
  WARNING,
  /** Skip the unresolved handler without reporting a diagnostic. */
  SILENT
}
