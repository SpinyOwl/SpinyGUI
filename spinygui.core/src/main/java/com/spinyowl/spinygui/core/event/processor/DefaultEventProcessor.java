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
  public InputImpact processEvents() {
    return processBatch().impact();
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private InputProcessingBatch processBatch() {
    InputProcessingBatch batch = new InputProcessingBatch();
    if (first.isEmpty()) {
      inputProcessingCounters.record(batch);
      return batch;
    }

    swap();
    for (var event = second.poll(); event != null; event = second.poll()) {
      EventTarget target = event.target();
      var listeners = target.getListeners(event.getClass());
      if (listeners.isEmpty()) {
        if (event instanceof com.spinyowl.spinygui.core.event.CursorEnterEvent
            || event instanceof com.spinyowl.spinygui.core.event.CursorExitEvent) {
          // Hover state was changed by the system cursor listener. With no dispatched listener,
          // there is no additional unknown application effect.
          batch.markHoverStyleEffect();
        } else {
          batch.markUnknownFallback();
        }
      } else {
        for (EventListener listener : listeners) {
          listener.processWithImpact(event, batch);
        }
      }
    }
    inputProcessingCounters.record(batch);
    return batch;
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
