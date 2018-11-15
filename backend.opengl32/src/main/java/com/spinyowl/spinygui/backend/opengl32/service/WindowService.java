package com.spinyowl.spinygui.backend.opengl32.service;

import com.spinyowl.spinygui.backend.opengl32.WindowOpenGL32;
import com.spinyowl.spinygui.core.api.Monitor;
import com.spinyowl.spinygui.core.api.Window;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class WindowService {

    private static final Map<Long, Window> WINDOW_CACHE = new ConcurrentHashMap<>();

    static List<Long> getWindowPointers() {
        return new ArrayList<>(WINDOW_CACHE.keySet());
    }

    static Window createWindow(int width, int height, String title, Monitor monitor) {
        long windowPointer = GLFW.glfwCreateWindow(width, height, title, monitor == null ? 0 : monitor.getPointer(), 0);
        if (windowPointer == 0) {
            //TODO THROW
            return null;
        }
        WindowOpenGL32 window = new WindowOpenGL32(windowPointer, width, height, title, monitor);
        GLFW.glfwSetWindowCloseCallback(windowPointer, (w) -> {
            SpinyGuiOpenGL32Service.getInstance().destroyWindow(window);
        });
        WINDOW_CACHE.put(windowPointer, window);
        return window;
    }

    static void destroyWindow(Window window) {
        long pointer = window.getPointer();
        WINDOW_CACHE.remove(pointer, window);
        GLFW.glfwDestroyWindow(pointer);
    }

    static List<Window> getWindows() {
        return new ArrayList<>(WINDOW_CACHE.values());
    }
}
