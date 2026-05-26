package com.spinyowl.spinygui.core.event.processor;

import com.spinyowl.spinygui.core.event.Event;
import com.spinyowl.spinygui.core.event.EventTarget;
import com.spinyowl.spinygui.core.event.listener.EventListener;
import java.util.LinkedList;
import java.util.Queue;

public class DefaultEventProcessor implements EventProcessor {

  private Queue<Event> first = new LinkedList<>();
  private Queue<Event> second = new LinkedList<>();

  @Override
  public void push(Event event) {
    first.add(event);
  }

  @Override
  @SuppressWarnings({"rawtypes", "unchecked"})
  public void processEvents() {
    if (first.isEmpty()) {
      return;
    }

    swap();
    for (var event = second.poll(); event != null; event = second.poll()) {
      EventTarget target = event.target();
      var listeners = target.getListeners(event.getClass());
      for (EventListener listener : listeners) {
        listener.process(event);
      }
    }
  }

  private void swap() {
    Queue<Event> queue = this.first;
    this.first = second;
    this.second = queue;
  }
}
