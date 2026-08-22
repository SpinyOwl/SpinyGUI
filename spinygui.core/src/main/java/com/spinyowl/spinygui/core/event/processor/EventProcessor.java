package com.spinyowl.spinygui.core.event.processor;

import com.spinyowl.spinygui.core.event.Event;

public interface EventProcessor {

  void push(Event event);

  /** Processes one queued batch and returns its conservative presentation impact. */
  InputImpact processEvents();

  /** Returns cumulative batch-level input decision counters. */
  default InputProcessingCounters.Snapshot inputProcessingCounters() {
    return InputProcessingCounters.Snapshot.EMPTY;
  }
}
