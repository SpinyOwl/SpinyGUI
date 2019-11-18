package com.spinyowl.spinygui.backend.core.event;

import com.spinyowl.spinygui.backend.core.event.handler.SystemEventHandler;
import com.spinyowl.spinygui.backend.core.event.handler.SystemEventHandlers;

/**
 * Marker interface that defines tree of system events.
 */
public abstract class SystemEvent {

    private SystemEventHandler eventHandler;

    public SystemEvent(SystemEventHandler systemEventHandler) {
        setEventHandler(this, systemEventHandler);
    }

    public SystemEvent() {
        this.eventHandler = SystemEventHandlers.getHandler(getClass());
    }

    public static <T extends SystemEvent> void setEventHandler(T event, SystemEventHandler<? extends T> eventHandler) {
        event.setEventHandler(SystemEventHandlers.getHandler(event.getClass()));
    }

    public static <T extends SystemEvent> SystemEventHandler<T> getEventHandler(T event) {
        return event.getEventHandler();
    }

    final SystemEventHandler getEventHandler() {
        return eventHandler;
    }

    final <T extends SystemEvent> void setEventHandler(SystemEventHandler<? extends T> eventHandler) {
        this.eventHandler = eventHandler;
    }

}
