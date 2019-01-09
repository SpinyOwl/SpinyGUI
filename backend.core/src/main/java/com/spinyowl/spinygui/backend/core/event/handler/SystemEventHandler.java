package com.spinyowl.spinygui.backend.core.event.handler;

import com.spinyowl.spinygui.backend.core.event.SystemEvent;

public interface SystemEventHandler<E extends SystemEvent> {

    void handle(E event);
}