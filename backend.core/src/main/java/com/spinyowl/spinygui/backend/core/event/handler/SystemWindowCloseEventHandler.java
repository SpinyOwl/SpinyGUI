package com.spinyowl.spinygui.backend.core.event.handler;

import com.spinyowl.spinygui.backend.core.event.SystemWindowCloseEvent;
import com.spinyowl.spinygui.core.event.WindowCloseEvent;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.system.service.ServiceHolder;

public class SystemWindowCloseEventHandler implements SystemEventHandler<SystemWindowCloseEvent> {
    @Override
    public void handle(SystemWindowCloseEvent event) {
        EventProcessor.getInstance().pushEvent(new WindowCloseEvent(ServiceHolder.getWindowService().getWindow(event.window)));
    }
}
