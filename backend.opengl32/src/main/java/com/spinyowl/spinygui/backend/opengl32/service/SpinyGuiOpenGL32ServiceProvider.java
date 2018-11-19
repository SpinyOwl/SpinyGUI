package com.spinyowl.spinygui.backend.opengl32.service;

import com.spinyowl.spinygui.core.service.MonitorService;
import com.spinyowl.spinygui.core.service.ServiceProvider;
import com.spinyowl.spinygui.core.service.WindowService;

public class SpinyGuiOpenGL32ServiceProvider implements ServiceProvider {

    private MonitorService monitorService = SpinyGuiOpenGL32MonitorService.getInstance();
    private WindowService windowService = SpinyGuiOpenGL32WindowService.getInstance();

    @Override
    public MonitorService getMonitorService() {
        return monitorService;
    }

    @Override
    public WindowService getWindowService() {
        return windowService;
    }

}
