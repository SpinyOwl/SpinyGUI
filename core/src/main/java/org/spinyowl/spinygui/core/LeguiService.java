package org.spinyowl.spinygui.core;

import org.spinyowl.spinygui.core.api.Monitor;
import org.spinyowl.spinygui.core.api.Window;

import java.util.List;

public abstract class SpinyGuiService {

    protected abstract void startService();

    protected abstract void stopService();

    public abstract Monitor getPrimaryMonitor();

    public abstract List<Monitor> getMonitors();

    public abstract Window createWindow(int width, int height, String title, Monitor monitor);

    public abstract void destroyWindow(Window window);

    public abstract List<Window> getWindows();

}
