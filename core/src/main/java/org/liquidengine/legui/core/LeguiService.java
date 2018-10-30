package org.liquidengine.legui.core;

import org.liquidengine.legui.core.api.Monitor;
import org.liquidengine.legui.core.api.Window;

import java.util.List;

public interface LeguiService {

    Monitor getPrimaryMonitor();

    List<Monitor> getMonitors();

    Window createWindow(int width, int height, String title, Monitor monitor);

    void destroyWindow(Window window);

    List<Window> getWindows();

}
