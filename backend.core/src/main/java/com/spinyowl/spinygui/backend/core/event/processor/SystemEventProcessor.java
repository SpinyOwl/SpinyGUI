package com.spinyowl.spinygui.backend.core.event.processor;

import com.spinyowl.spinygui.backend.core.event.SystemEvent;

public interface SystemEventProcessor {

    /**
     * Used to process system event.
     */
    void processEvents();

    /**
     * Used to push system event to processing queue.
     *
     * @param event event to push.
     */
    void pushEvent(SystemEvent event);

}
