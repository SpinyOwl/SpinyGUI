package com.spinyowl.spinygui.core.event.processor;

public final class EventProcessorProvider {
    public static EventProcessor getInstance() {
        return EPH.instance;
    }

    private EventProcessorProvider(){}

    public static void setInstance(EventProcessor eventProcessor) {
        if (eventProcessor != null) {
            EPH.instance = eventProcessor;
        }
    }

    private static final class EPH {
        private static EventProcessor instance = new DefaultEventProcessor();
    }
}
