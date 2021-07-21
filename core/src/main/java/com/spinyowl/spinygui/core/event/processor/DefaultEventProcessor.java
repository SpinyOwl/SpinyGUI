package com.spinyowl.spinygui.core.event.processor;

import com.spinyowl.spinygui.core.event.Event;
import com.spinyowl.spinygui.core.event.EventTarget;
import com.spinyowl.spinygui.core.event.listener.EventListener;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class DefaultEventProcessor implements EventProcessor {

  private Queue<Event> eventQueueFirst = new LinkedBlockingQueue<>();
  private Queue<Event> eventQueueSecond = new LinkedBlockingQueue<>();

  @Override
  public void push(Event event) {
    eventQueueFirst.add(event);
  }

  @Override
  @SuppressWarnings({"rawtypes", "unchecked"})
  public void processEvents() {
    swap();
    for (var event : eventQueueSecond) {
      EventTarget target = event.target();
      var listeners = target.getListeners(event.getClass());
      for (EventListener listener : listeners) {
        listener.process(event);
      }
    }
    eventQueueSecond.clear();
  }

  private void swap() {
    Queue<Event> queue = this.eventQueueFirst;
    this.eventQueueFirst = eventQueueSecond;
    this.eventQueueSecond = queue;
  }
}
