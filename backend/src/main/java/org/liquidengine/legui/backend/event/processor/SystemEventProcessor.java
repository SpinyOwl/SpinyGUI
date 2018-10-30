package org.liquidengine.legui.backend.event.processor;

import org.liquidengine.legui.backend.event.SystemEvent;

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
