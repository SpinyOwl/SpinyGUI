package org.spinyowl.spinygui.core.event.processor;

public abstract class EventProcessor {
    public static EventProcessor getInstance() {
        return EPH.INSTANCE;
    }

    private static final class EPH {
        public static final EventProcessor INSTANCE = new EventProcessor() {
        };
    }
}
