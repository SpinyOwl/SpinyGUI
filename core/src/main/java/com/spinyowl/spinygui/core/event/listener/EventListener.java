package com.spinyowl.spinygui.core.event.listener;

import com.spinyowl.spinygui.core.event.Event;

public interface EventListener<T extends Event> {

  void process(T event);

}
