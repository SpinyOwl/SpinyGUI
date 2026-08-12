package com.spinyowl.spinygui.core.system.event.processor;

import com.spinyowl.spinygui.core.event.processor.InputProcessingCounters;
import com.spinyowl.spinygui.core.event.processor.InputProcessingResult;
import com.spinyowl.spinygui.core.system.event.SystemEvent;

/**
 * System event processor. Used to store system events as a queue and process them (by delegating to
 * {@link com.spinyowl.spinygui.core.system.event.listener.SystemEventListener
 * SystemEventListeners}).
 */
public interface SystemEventProcessor {

  /** Used to process stored events in system event processor. */
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

  /**
   * Used to push new system event to {@link SystemEventProcessor}.
   *
   * @param event system event to push to queue.
   */
  void push(SystemEvent event);

  /**
   * Used to check if current system event processor has any system events to process.
   *
   * @return true if there is any not processed system event.
   */
  boolean hasEvents();
}
