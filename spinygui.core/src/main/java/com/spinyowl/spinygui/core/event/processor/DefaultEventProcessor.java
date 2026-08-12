package com.spinyowl.spinygui.core.event.processor;

import com.spinyowl.spinygui.core.event.Event;
import com.spinyowl.spinygui.core.event.EventTarget;
import com.spinyowl.spinygui.core.event.listener.EventListener;
import java.util.LinkedList;
import java.util.Queue;

public class DefaultEventProcessor implements EventProcessor {

  private Queue<Event> first = new LinkedList<>();
  private Queue<Event> second = new LinkedList<>();
  private final InputProcessingCounters inputProcessingCounters = new InputProcessingCounters();

  @Override
  public void push(Event event) {
    first.add(event);
  }

  @Override
  @SuppressWarnings({"rawtypes", "unchecked"})
  public void processEvents() {
    processEventsWithResult();
  }

  @Override
  @SuppressWarnings({"rawtypes", "unchecked"})
  public InputProcessingResult processEventsWithResult() {
    InputProcessingBatch batch = new InputProcessingBatch();
    if (first.isEmpty()) {
      inputProcessingCounters.record(batch);
      return batch.result();
    }

    swap();
    for (var event = second.poll(); event != null; event = second.poll()) {
      EventTarget target = event.target();
      var listeners = target.getListeners(event.getClass());
      if (listeners.isEmpty()) {
        batch.markUnknownFallback();
      } else {
        for (EventListener listener : listeners) {
          listener.processWithImpact(event, batch);
        }
      }
    }
    inputProcessingCounters.record(batch);
    return batch.result();
  }

  @Override
  public InputProcessingCounters.Snapshot inputProcessingCounters() {
    return inputProcessingCounters.snapshot();
  }

  private void swap() {
    Queue<Event> queue = this.first;
    this.first = second;
    this.second = queue;
  }
}
