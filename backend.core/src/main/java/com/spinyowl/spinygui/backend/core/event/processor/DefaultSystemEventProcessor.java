package com.spinyowl.spinygui.backend.core.event.processor;

import com.spinyowl.spinygui.backend.core.event.SystemEvent;
import com.spinyowl.spinygui.backend.core.event.SystemWindowCloseEvent;
import com.spinyowl.spinygui.backend.core.event.handler.SystemEventHandler;
import com.spinyowl.spinygui.backend.core.event.handler.SystemWindowCloseEventHandler;

import java.util.ArrayList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class DefaultSystemEventProcessor extends SystemEventProcessor {

    private Queue<SystemEvent> eventQueue = new LinkedBlockingQueue<>();
    private Map<Class<? extends SystemEvent>, SystemEventHandler<? extends SystemEvent>> eventHandlerMap = new ConcurrentHashMap<>();

    {
        setHandler(SystemWindowCloseEvent.class, new SystemWindowCloseEventHandler());
    }

    @Override
    public void pushEvent(SystemEvent event) {
        eventQueue.add(event);
    }

    @Override
    public void processEvents() {
        var events = new ArrayList<>(eventQueue);
        for (var event : events) {
            this.processEvent(event);
        }
        eventQueue.removeAll(events);
    }


    private void processEvent(SystemEvent event) {
        SystemEventHandler handler = getHandler(event.getClass());
        if (handler != null) {
            System.out.println("PROCESS EVENT: " + event);
            handler.handle(event);
        }
    }

    public <T extends SystemEvent, H extends SystemEventHandler<T>> void setHandler(Class<T> clazz, H handler) {
        eventHandlerMap.put(clazz, handler);
    }

    public <T extends SystemEvent, H extends SystemEventHandler<T>> H getHandler(Class<T> clazz) {
        return (H) eventHandlerMap.get(clazz);
    }
}
