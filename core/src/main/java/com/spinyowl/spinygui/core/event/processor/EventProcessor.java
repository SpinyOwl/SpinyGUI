package com.spinyowl.spinygui.core.event.processor;

import com.spinyowl.spinygui.core.event.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class EventProcessor {
    private static EventProcessor instance = new EventProcessor() {
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

        }
    };

    public static EventProcessor getInstance() {
        return instance;
    }

    public static void setInstance(EventProcessor eventProcessor) {
        if (eventProcessor != null) instance = eventProcessor;
    }

    public abstract void pushEvent(Event event);

    public abstract void processEvents();

}
