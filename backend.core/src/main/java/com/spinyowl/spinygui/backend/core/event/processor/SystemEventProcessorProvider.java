package com.spinyowl.spinygui.backend.core.event.processor;

public final class SystemEventProcessorProvider {
    private SystemEventProcessorProvider() {
    }

    public static SystemEventProcessor getSystemEventProcessor() {
        return SEPPH.systemEventProcessor;
    }

    public static void setSystemEventProcessor(SystemEventProcessor systemEventProcessor) {
        SystemEventProcessorProvider.SEPPH.systemEventProcessor = systemEventProcessor;
    }

    private static final class SEPPH {
        private static SystemEventProcessor systemEventProcessor = new DefaultSystemEventProcessor();
    }
}
