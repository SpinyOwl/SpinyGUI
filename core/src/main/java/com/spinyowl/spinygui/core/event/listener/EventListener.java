package com.spinyowl.spinygui.core.event.listener;

import com.spinyowl.spinygui.core.event.NodeEvent;

public interface EventListener<T extends NodeEvent> {

  void process(T event);
}
