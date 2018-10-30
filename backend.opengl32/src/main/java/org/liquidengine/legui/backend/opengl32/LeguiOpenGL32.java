package org.liquidengine.legui.backend.opengl32;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.liquidengine.legui.core.LeguiService;
import org.liquidengine.legui.core.api.Monitor;
import org.liquidengine.legui.core.api.Window;

import java.util.List;

public class LeguiOpenGL32 implements LeguiService {
    private static final Logger LOGGER = LogManager.getLogger();

    static {
        LOGGER.debug("INITIALIZED WITH OpenGL 32 CORE");
    }


    public LeguiOpenGL32() {
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
