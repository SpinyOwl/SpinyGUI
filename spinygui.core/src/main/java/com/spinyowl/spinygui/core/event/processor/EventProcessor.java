package com.spinyowl.spinygui.core.event.processor;

import com.spinyowl.spinygui.core.event.Event;

public interface EventProcessor {

  void push(Event event);

  void processEvents();

  /**
   * Processes one queued batch and returns its conservative presentation-impact result.
   *
   * <p>Implementations predating this method retain the safe fallback when invoked through the
   * default adapter.
   */
  default InputProcessingResult processEventsWithResult() {
    processEvents();
    return InputProcessingResult.FULL_REFRESH_REQUIRED;
  }

  /** Returns cumulative batch-level input decision counters. */
  default InputProcessingCounters.Snapshot inputProcessingCounters() {
    return InputProcessingCounters.Snapshot.EMPTY;
  }
}
