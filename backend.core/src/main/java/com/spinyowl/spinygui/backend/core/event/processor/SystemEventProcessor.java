package com.spinyowl.spinygui.backend.core.event.processor;

import com.spinyowl.spinygui.backend.core.event.SystemEvent;

public abstract class SystemEventProcessor {

    public static SystemEventProcessor getInstance() {
        return SEPH.INSTANCE;
    }

    public static void setInstance(SystemEventProcessor instance) {
        if (instance != null) SEPH.INSTANCE = instance;
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

    private static final class SEPH {
        private static SystemEventProcessor INSTANCE = new DefaultSystemEventProcessor();
    }
}
