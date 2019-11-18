package com.spinyowl.spinygui.core.window.service;

import com.spinyowl.spinygui.core.window.Monitor;
import com.spinyowl.spinygui.core.window.Window;
import org.joml.Vector2d;
import org.joml.Vector2i;

import java.util.List;

/**
 * Window service allows to work with window and it's internal state.
 * Mostly used by {@link Window} class.
 */
public interface WindowService {

    /**
     * Used to create window with specified parameters.
     *
     * @param width  window width.
     * @param height window height.
     * @param title  window title.
     * @return
     */
    Window createWindow(int width, int height, String title);

    /**
     * Used to create fullscreen window with specified parameters.
     *
     * @param width   window width.
     * @param height  window height.
     * @param title   window title.
     * @param monitor monitor for fullscreen window.
     * @return
     */
    Window createWindow(int width, int height, String title, Monitor monitor);

    /**
     * Used to close window.
     *
     * @param window window to close.
     * @return true if window is closed.
     */
    boolean closeWindow(Window window);

    Window getWindow(long pointer);

    Vector2i getWindowSize(Window window);

    void setWindowSize(Window window, Vector2i size);

    Vector2i getWindowPosition(Window window);

    void setWindowPosition(Window window, Vector2i position);

    boolean isWindowVisible(Window window);

    void setWindowVisible(Window window, boolean visible);

    void setWindowTitle(Window window, String title);

    Vector2d getCursorPosition(Window window);

    void setCursorPosition(Window window, Vector2d position);

    List<Window> getWindows();

}
