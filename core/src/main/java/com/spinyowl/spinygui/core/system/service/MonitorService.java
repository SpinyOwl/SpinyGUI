package com.spinyowl.spinygui.core.system.service;

import com.spinyowl.spinygui.core.api.Monitor;

import java.util.List;

/**
 * Monitor service allows to
 */
public interface MonitorService {

    /**
     * Returns primary monitor instance. Should not create new object for every method call.
     *
     * @return primary monitor.
     */
    Monitor getPrimaryMonitor();

    /**
     * Returns all monitors that currently
     *
     * @return
     */
    List<Monitor> getMonitors();

}
