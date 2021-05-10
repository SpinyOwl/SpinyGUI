package com.spinyowl.spinygui.core.event.processor;

import com.spinyowl.spinygui.core.event.Event;
import com.spinyowl.spinygui.core.event.EventTarget;
import com.spinyowl.spinygui.core.event.listener.EventListener;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class DefaultEventProcessor implements EventProcessor {

  private final Queue<Event> eventQueue = new LinkedBlockingQueue<>();

  @Override
  public void push(Event event) {
    eventQueue.add(event);
  }

  @Override
  public void processEvents() {
    var events = List.copyOf(eventQueue);

    for (var event : events) {
      EventTarget target = event.target();
      var listeners = target.getListeners(event.getClass());
      for (EventListener listener : listeners) {
        listener.process(event);
      }
    }

    eventQueue.removeAll(events);
  }
}
