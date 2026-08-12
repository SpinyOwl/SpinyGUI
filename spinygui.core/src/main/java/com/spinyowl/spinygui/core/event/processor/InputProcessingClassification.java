package com.spinyowl.spinygui.core.event.processor;

/** Internal reason category retained for batch-level input observability. */
public enum InputProcessingClassification {
  /** No event in the batch was found to require refresh. */
  PROVEN_UNCHANGED,

  /** At least one event was classified as an actual presentation effect. */
  KNOWN_EFFECT,

  /** At least one event or listener was not safe to classify optimistically. */
  UNKNOWN_FALLBACK
}
