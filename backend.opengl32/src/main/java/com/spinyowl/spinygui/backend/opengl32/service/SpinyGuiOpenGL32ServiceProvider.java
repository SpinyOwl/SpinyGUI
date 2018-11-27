package com.spinyowl.spinygui.backend.opengl32.service;

import com.spinyowl.spinygui.core.service.ClipboardService;
import com.spinyowl.spinygui.core.service.MonitorService;
import com.spinyowl.spinygui.core.service.ServiceHolder;
import com.spinyowl.spinygui.core.service.WindowService;

public class SpinyGuiOpenGL32ServiceProvider implements ServiceHolder.ServiceProvider {

    private MonitorService monitorService = SpinyGuiOpenGL32MonitorService.getInstance();
    private WindowService windowService = SpinyGuiOpenGL32WindowService.getInstance();
    private ClipboardService clipboardService = SpinyGuiOpenGL32ClipboardService.getInstance();

    @Override
    public MonitorService getMonitorService() {
        return monitorService;
    }

    @Override
    public WindowService getWindowService() {
        return windowService;
    }

    @Override
    public ClipboardService getClipboardService() {
        return clipboardService;
    }

}
