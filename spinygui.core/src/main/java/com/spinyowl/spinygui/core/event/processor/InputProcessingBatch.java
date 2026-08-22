package com.spinyowl.spinygui.core.event.processor;

/** Mutable owner-thread state used while one input-processing batch is dispatched. */
public final class InputProcessingBatch {
  private InputProcessingClassification classification =
      InputProcessingClassification.PROVEN_UNCHANGED;
  private InputImpact impact = InputImpact.NO_IMPACT;

  /** Records an effect that is known to require a full refresh. */
  public void markKnownEffect() {
    impact = impact.combine(InputImpact.FULL_REFRESH);
    if (classification == InputProcessingClassification.PROVEN_UNCHANGED) {
      classification = InputProcessingClassification.KNOWN_EFFECT;
    }
  }

  /** Records a hover pseudo-state transition whose resolved style still requires classification. */
  public void markHoverStyleEffect() {
    impact = impact.combine(InputImpact.HOVER_STYLE);
    if (classification == InputProcessingClassification.PROVEN_UNCHANGED) {
      classification = InputProcessingClassification.KNOWN_EFFECT;
    }
  }

  /** Records an unclassified path that must retain the full-refresh fallback. */
  public void markUnknownFallback() {
    impact = InputImpact.FULL_UNKNOWN;
    classification = InputProcessingClassification.UNKNOWN_FALLBACK;
  }

  public InputProcessingClassification classification() {
    return classification;
  }

  public InputImpact impact() {
    return impact;
  }
}
