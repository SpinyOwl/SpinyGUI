package com.spinyowl.spinygui.core.event.processor;

import com.spinyowl.spinygui.core.event.Event;

public abstract class EventProcessor {
    public static EventProcessor getInstance() {
        return EPH.INSTANCE;
    }

    public static void setInstance(EventProcessor eventProcessor) {
        if (eventProcessor != null) EPH.INSTANCE = eventProcessor;
    }

    public abstract void pushEvent(Event event);

    public abstract void processEvents();

    private static final class EPH {
        private static EventProcessor INSTANCE = new DefaultEventProcessor();
    }

}
