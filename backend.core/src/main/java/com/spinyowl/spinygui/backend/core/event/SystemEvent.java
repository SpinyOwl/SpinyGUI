package com.spinyowl.spinygui.backend.core.event;

import com.spinyowl.spinygui.backend.core.event.handler.SystemEventHandler;
import com.spinyowl.spinygui.backend.core.event.handler.SystemEventHandlers;

/**
 * Marker interface that defines tree of system events.
 */
public abstract class SystemEvent {

    private final SystemEventHandler eventHandler;

    public SystemEvent(SystemEventHandler systemEventHandler) {
        this.eventHandler = systemEventHandler;
    }

    public SystemEvent() {
        this.eventHandler = SystemEventHandlers.getHandler(getClass());
    }

    public SystemEventHandler getEventHandler() {
        return eventHandler;
    }
}
