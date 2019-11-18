package com.spinyowl.spinygui.backend.core.event.processor;

import com.spinyowl.spinygui.backend.core.event.SystemEvent;
import com.spinyowl.spinygui.backend.core.event.handler.SystemEventHandler;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class DefaultSystemEventProcessor implements SystemEventProcessor {

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
        SystemEventHandler<SystemEvent> handler = SystemEvent.getEventHandler(event);
        if (handler != null) {
            handler.handle(event);
        }
    }


}
