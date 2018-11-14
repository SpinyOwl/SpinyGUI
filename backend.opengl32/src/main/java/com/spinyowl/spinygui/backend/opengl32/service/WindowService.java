package com.spinyowl.spinygui.backend.opengl32.service;

import com.spinyowl.spinygui.backend.opengl32.WindowOpenGL32;
import com.spinyowl.spinygui.core.api.Monitor;
import com.spinyowl.spinygui.core.api.Window;
import org.lwjgl.glfw.GLFW;

class WindowService {

    static Window createWindow(int width, int height, String title, Monitor monitor) {
        long windowPointer = GLFW.glfwCreateWindow(width, height, title, monitor == null ? 0 : monitor.getPointer(), 0);
        if (windowPointer == 0) {
            //TODO THROW
            return null;
        }
        return new WindowOpenGL32(windowPointer, width, height, title, monitor);
    }

    static void destroyWindow(Window window) {
        GLFW.glfwDestroyWindow(window.getPointer());
    }
}
