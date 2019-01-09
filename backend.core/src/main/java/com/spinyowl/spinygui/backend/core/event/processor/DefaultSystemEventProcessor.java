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
        SystemEventHandler handler = event.getEventHandler();
        if (handler != null) {
            System.out.println("PROCESS EVENT: " + event);
            handler.handle(event);
        }
    }


}
