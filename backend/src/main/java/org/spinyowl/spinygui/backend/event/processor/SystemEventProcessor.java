package org.spinyowl.spinygui.backend.event.processor;

import org.spinyowl.spinygui.backend.event.SystemEvent;

public interface SystemEventProcessor {

    /**
     * Used to process system event.
     */
    void processEvent();

    /**
     * Used to push system event to processing queue.
     *
     * @param event event to push.
     */
    void pushEvent(SystemEvent event);
}
