package com.spinyowl.spinygui.core.event.processor;

public final class EventProcessorProvider {

    private EventProcessorProvider() {
    }

    public static EventProcessor getInstance() {
        return EPH.instance;
    }

    public static void setInstance(EventProcessor eventProcessor) {
        if (eventProcessor != null) {
            EPH.instance = eventProcessor;
        }
    }

    private static final class EPH {

        private static EventProcessor instance = new DefaultEventProcessor();
    }
}
