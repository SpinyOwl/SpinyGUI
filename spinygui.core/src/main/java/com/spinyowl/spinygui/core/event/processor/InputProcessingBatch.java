package com.spinyowl.spinygui.core.event.processor;

/** Mutable owner-thread state used while one input-processing batch is dispatched. */
public final class InputProcessingBatch {
  private InputProcessingClassification classification =
      InputProcessingClassification.PROVEN_UNCHANGED;

  /** Records an effect that is known to require a full refresh. */
  public void markKnownEffect() {
    if (classification == InputProcessingClassification.PROVEN_UNCHANGED) {
      classification = InputProcessingClassification.KNOWN_EFFECT;
    }
  }

  /** Records an unclassified path that must retain the full-refresh fallback. */
  public void markUnknownFallback() {
    classification = InputProcessingClassification.UNKNOWN_FALLBACK;
  }

  public InputProcessingClassification classification() {
    return classification;
  }

  public InputProcessingResult result() {
    return classification == InputProcessingClassification.PROVEN_UNCHANGED
        ? InputProcessingResult.UNCHANGED
        : InputProcessingResult.FULL_REFRESH_REQUIRED;
  }
}
