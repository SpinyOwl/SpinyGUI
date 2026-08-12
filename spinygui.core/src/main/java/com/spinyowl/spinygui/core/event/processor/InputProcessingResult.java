package com.spinyowl.spinygui.core.event.processor;

/** Conservative result of processing one input batch. */
public enum InputProcessingResult {
  /** Processing proved that presentation could not have changed. */
  UNCHANGED,

  /** Processing changed presentation or could not prove that it did not. */
  FULL_REFRESH_REQUIRED;

  /** Combines two results; a full refresh requirement always dominates. */
  public static InputProcessingResult aggregate(
      InputProcessingResult first, InputProcessingResult second) {
    if (first == null || second == null) {
      throw new NullPointerException("Input processing results must not be null");
    }
    return first == FULL_REFRESH_REQUIRED || second == FULL_REFRESH_REQUIRED
        ? FULL_REFRESH_REQUIRED
        : UNCHANGED;
  }
}
