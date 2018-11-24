package com.spinyowl.spinygui.core.event.processor;

import com.spinyowl.spinygui.core.api.Window;
import com.spinyowl.spinygui.core.event.Event;
import com.spinyowl.spinygui.core.event.WindowCloseEvent;
import com.spinyowl.spinygui.core.event.listener.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class DefaultEventProcessor extends EventProcessor {
    private Queue<Event> eventQueue = new LinkedBlockingQueue<>();

    @Override
    public void pushEvent(Event event) {
        eventQueue.add(event);
    }

    @Override
    public void processEvents() {
        List<Event> events = new ArrayList<>(eventQueue);
        for (Event event : events) {
            processEvent(event);
        }
        eventQueue.removeAll(events);
    }

    private void processEvent(Event event) {
        if (event instanceof WindowCloseEvent) {
            WindowCloseEvent wce = (WindowCloseEvent) event;
            Window target = wce.target;
            for (Listener<WindowCloseEvent> listener : target.getWindowCloseEventListeners()) {
                listener.process(wce);
            }
        }
    }
}
