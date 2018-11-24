package com.spinyowl.spinygui.backend.core.event.handler;

import com.spinyowl.spinygui.backend.core.event.SystemWindowCloseEvent;
import com.spinyowl.spinygui.core.event.WindowCloseEvent;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;

public class SystemWindowCloseEventHandler implements SystemEventHandler<SystemWindowCloseEvent> {
    @Override
    public void handle(SystemWindowCloseEvent event) {
        EventProcessor.getInstance().pushEvent(new WindowCloseEvent(event.window));
    }
}
