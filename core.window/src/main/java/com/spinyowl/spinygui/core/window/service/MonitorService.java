package com.spinyowl.spinygui.core.window.service;

import com.spinyowl.spinygui.core.window.Monitor;
import java.util.List;

/**
 * Monitor service allows to work with monitors. Mostly used by {@link Monitor} class.
 */
public interface MonitorService {

    /**
     * Returns primary monitor instance. Should not create new object for every method call.
     *
     * @return primary monitor.
     */
    Monitor getPrimaryMonitor();

    /**
     * Returns all monitors.
     *
     * @return all monitors.
     */
    List<Monitor> getMonitors();

}
