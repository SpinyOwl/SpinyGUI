package com.spinyowl.spinygui.core.event.processor;

import com.spinyowl.spinygui.core.event.Event;
import com.spinyowl.spinygui.core.event.EventTarget;
import com.spinyowl.spinygui.core.event.listener.EventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class DefaultEventProcessor implements EventProcessor {

  private Queue<Event> eventQueue = new LinkedBlockingQueue<>();

  @Override
  public void pushEvent(Event event) {
    eventQueue.add(event);
  }

  @Override
  public void processEvents() {
    List<Event> events = new ArrayList<>(eventQueue);

    for (Event event : events) {
      EventTarget target = event.getTarget();
      if (target != null) {
        List<? extends EventListener<? extends Event>> listeners = target
            .getListeners(event.getClass());
        for (EventListener listener : listeners) {
          listener.process(event);
        }
      }
    }

    eventQueue.removeAll(events);
  }

}
