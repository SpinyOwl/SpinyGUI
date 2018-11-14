package com.spinyowl.spinygui.backend.opengl32.service;

import com.spinyowl.spinygui.core.SpinyGuiService;
import com.spinyowl.spinygui.core.api.Monitor;
import com.spinyowl.spinygui.core.api.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SpinyGuiOpenGL32Wrapper extends SpinyGuiService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpinyGuiOpenGL32Wrapper.class);
    private static final SpinyGuiOpenGL32Service service = SpinyGuiOpenGL32Service.getInstance();

    static {
        LOGGER.debug("INITIALIZED WITH OpenGL 32 CORE");
    }

    public SpinyGuiOpenGL32Wrapper() {
        LOGGER.debug("CREATED INSTANCE OF " + getClass().getName());
    }

    @Override
    protected void startService() {
        service.startService();
    }

    @Override
    protected void stopService() {
        service.stopService();
    }

    @Override
    public Monitor getPrimaryMonitor() {
        long startTime = System.nanoTime();
        LOGGER.info("Started getPrimaryMonitor " + startTime);
        try {
            return service.getPrimaryMonitor();
        } finally {
            long endTime = System.nanoTime();
            LOGGER.info("Finished getPrimaryMonitor " + endTime + " " + (endTime - startTime));
        }
    }

    @Override
    public List<Monitor> getMonitors() {
        return service.getMonitors();
    }

    @Override
    public Window createWindow(int width, int height, String title) {
        return service.createWindow(width, height, title);
    }

    @Override
    public Window createWindow(int width, int height, String title, Monitor monitor) {
        return service.createWindow(width, height, title, monitor);
    }

    @Override
    public void destroyWindow(Window window) {
        service.destroyWindow(window);
    }

    @Override
    public List<Window> getWindows() {
        return service.getWindows();
    }
}
