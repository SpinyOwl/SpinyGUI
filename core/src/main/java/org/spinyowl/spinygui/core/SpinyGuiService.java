package org.spinyowl.spinygui.core;

import org.spinyowl.spinygui.core.api.Monitor;
import org.spinyowl.spinygui.core.api.Window;

import java.util.List;

public abstract class SpinyGuiService {

    /**
     * Should be used internally to start service.
     */
    protected abstract void startService();

    /**
     * Should be used internally to stop service. Aimed to use with Runtime.addShutdownHook();
     */
    protected abstract void stopService();

    /**
     * Returns primary monitor instance. Should not create new object for every method call.
     *
     * @return primary monitor.
     */
    public abstract Monitor getPrimaryMonitor();

    /**
     * Returns all monitors that currently
     * @return
     */
    public abstract List<Monitor> getMonitors();

    public abstract Window createWindow(int width, int height, String title, Monitor monitor);

    public abstract void destroyWindow(Window window);

    public abstract List<Window> getWindows();

}
