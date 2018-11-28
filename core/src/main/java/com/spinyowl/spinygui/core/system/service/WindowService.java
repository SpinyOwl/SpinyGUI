package com.spinyowl.spinygui.core.system.service;

import com.spinyowl.spinygui.core.api.Monitor;
import com.spinyowl.spinygui.core.api.Window;
import org.joml.Vector2i;

public interface WindowService {

    Window createWindow(int width, int height, String title);

    Window createWindow(int width, int height, String title, Monitor monitor);

    boolean closeWindow(Window window);

    Window getWindow(long pointer);

    Vector2i getWindowSize(Window window);

    void setWindowSize(Window window, Vector2i size);

    Vector2i getWindowPosition(Window window);

    void setWindowPosition(Window window, Vector2i position);

    boolean isWindowVisible(Window window);

    void setWindowVisible(Window window, boolean visible);

    void setWindowTitle(Window window, String title);

    void getCursorPosition(Window window);

}
