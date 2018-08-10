package org.liquidengine.legui.core;

import org.liquidengine.legui.backend.Toolkit;

/**
 * Created by ShchAlexander on 08.08.2018.
 */
public class Window {

    static {
        // Ensure that all background initialized
        Toolkit.loadLibraries();
    }

    public Window(int width, int height, String title, boolean fullscreen) {

    }

    public Window(int width, int height, String title, Display display) {

    }

    public Window(int width, int height, String title) {
        this(width, height, title, false);
    }
}
