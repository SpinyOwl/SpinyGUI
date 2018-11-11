package org.spinyowl.spinygui.backend.opengl32;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spinyowl.spinygui.core.SpinyGuiService;
import org.spinyowl.spinygui.core.api.Monitor;
import org.spinyowl.spinygui.core.api.Window;

import java.util.List;

public class SpinyGuiOpenGL32 extends SpinyGuiService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpinyGuiOpenGL32.class);

    static {
        LOGGER.debug("INITIALIZED WITH OpenGL 32 CORE");
    }

    @Override
    protected void startService() {

    }

    @Override
    protected void stopService() {

    }

    public SpinyGuiOpenGL32() {
        LOGGER.debug("CREATED INSTANCE OF " + getClass().getName());
    }

    @Override
    public Monitor getPrimaryMonitor() {
        return null;
    }

    @Override
    public List<Monitor> getMonitors() {
        return null;
    }

    @Override
    public Window createWindow(int width, int height, String title, Monitor monitor) {
        return null;
    }

    @Override
    public void destroyWindow(Window window) {

    }

    @Override
    public List<Window> getWindows() {
        return null;
    }
}
