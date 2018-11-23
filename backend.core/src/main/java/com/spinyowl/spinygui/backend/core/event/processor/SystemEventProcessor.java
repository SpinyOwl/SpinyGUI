package com.spinyowl.spinygui.backend.core.event.processor;

import com.spinyowl.spinygui.backend.core.event.SystemEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class SystemEventProcessor {

    private static SystemEventProcessor INSTANCE = new SystemEventProcessor() {
        private Queue<SystemEvent> eventQueue = new LinkedBlockingQueue<>();

        @Override
        public void pushEvent(SystemEvent event) {
            eventQueue.add(event);
        }

        @Override
        public void processEvents() {
            List<SystemEvent> events = new ArrayList<>(eventQueue);
            for (SystemEvent event : events) {
                this.processEvent(event);
            }
            eventQueue.removeAll(events);
        }

        private void processEvent(SystemEvent event) {

        }
    };

    public static SystemEventProcessor getInstance() {
        return INSTANCE;
    }

    /**
     * Used to process system event.
     */
    public abstract void processEvents();

    /**
     * Used to push system event to processing queue.
     *
     * @param event event to push.
     */
    public abstract void pushEvent(SystemEvent event);
}
