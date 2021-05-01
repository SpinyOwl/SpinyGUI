package com.spinyowl.spinygui.core.event.processor;

import com.spinyowl.spinygui.core.event.Event;

public interface EventProcessor {

  void push(Event event);

  void processEvents();
}
