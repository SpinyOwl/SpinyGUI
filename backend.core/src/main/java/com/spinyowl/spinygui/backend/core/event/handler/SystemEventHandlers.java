package com.spinyowl.spinygui.backend.core.event.handler;

import com.spinyowl.spinygui.backend.core.event.SystemEvent;
import com.spinyowl.spinygui.backend.core.event.SystemWindowCloseEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class SystemEventHandlers {

    public static final SystemEventHandler<SystemWindowCloseEvent> DEFAULT_SYSTEM_WINDOW_CLOSE_EVENT_HANDLER = new SystemWindowCloseEventHandler();
    private static SystemEventHandler<SystemWindowCloseEvent> systemWindowCloseEventHandler = DEFAULT_SYSTEM_WINDOW_CLOSE_EVENT_HANDLER;

    private static Map<Class<? extends SystemEvent>, SystemEventHandler> eventHandlerMap = new ConcurrentHashMap<>();

    static {
        setHandler(SystemWindowCloseEvent.class, systemWindowCloseEventHandler);
    }

    private SystemEventHandlers() {
    }

    public static <T extends SystemEvent, H extends SystemEventHandler<T>> void setHandler(Class<T> clazz, H handler) {
        eventHandlerMap.put(clazz, handler);
    }

    public static <T extends SystemEvent> SystemEventHandler<T> getHandler(Class<T> clazz) {
        return (SystemEventHandler<T>) eventHandlerMap.get(clazz);
    }

    public static SystemEventHandler<SystemWindowCloseEvent> getSystemWindowCloseEventHandler() {
        return systemWindowCloseEventHandler;
    }

    public static void setSystemWindowCloseEventHandler(SystemEventHandler<SystemWindowCloseEvent> systemWindowCloseEventHandler) {
        if (systemWindowCloseEventHandler != null) {
            SystemEventHandlers.systemWindowCloseEventHandler = systemWindowCloseEventHandler;
        } else {
            SystemEventHandlers.systemWindowCloseEventHandler = DEFAULT_SYSTEM_WINDOW_CLOSE_EVENT_HANDLER;
        }
        setHandler(SystemWindowCloseEvent.class, SystemEventHandlers.systemWindowCloseEventHandler);
    }

}
