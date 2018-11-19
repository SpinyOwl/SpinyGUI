package com.spinyowl.spinygui.backend.opengl32.service;

import com.spinyowl.spinygui.backend.opengl32.api.WindowOpenGL32;
import com.spinyowl.spinygui.backend.opengl32.service.internal.SpinyGuiOpenGL32Service;
import com.spinyowl.spinygui.core.api.Monitor;
import com.spinyowl.spinygui.core.api.Window;
import com.spinyowl.spinygui.core.service.WindowService;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SpinyGuiOpenGL32WindowService implements WindowService {

    private static final SpinyGuiOpenGL32WindowService INSTANCE = new SpinyGuiOpenGL32WindowService();
    private static final Map<Long, Window> WINDOW_CACHE = new ConcurrentHashMap<>();

    static {
        SpinyGuiOpenGL32Service.getInstance().startService();
    }

    private SpinyGuiOpenGL32WindowService() {
    }

    public static SpinyGuiOpenGL32WindowService getInstance() {
        return INSTANCE;
    }

    @Override
    public Window createWindow(int width, int height, String title) {
        return createWindow(width, height, title, null);
    }

    @Override
    public Window createWindow(int width, int height, String title, Monitor monitor) {
        return SpinyGuiOpenGL32Service.getInstance().addTaskAndGet(() -> createWindowGL32(width, height, title, monitor));
    }

    private WindowOpenGL32 createWindowGL32(int width, int height, String title, Monitor monitor) {
        long windowPointer = GLFW.glfwCreateWindow(width, height, title, monitor == null ? 0 : monitor.getPointer(), 0);
        if (windowPointer == 0) {
            //TODO THROW
            return null;
        }
        WindowOpenGL32 window = new WindowOpenGL32(windowPointer, width, height, title, monitor);
        GLFW.glfwSetWindowCloseCallback(windowPointer, (w) -> destroyWindow(window));

        WINDOW_CACHE.put(windowPointer, window);
        return window;
    }

    public List<Window> getWindows() {
        return new ArrayList<>(WINDOW_CACHE.values());
    }


    public void destroyWindow(Window window) {
        SpinyGuiOpenGL32Service.getInstance().addTask(() -> {
            long pointer = window.getPointer();
            WINDOW_CACHE.remove(pointer, window);
            GLFW.glfwDestroyWindow(pointer);
        });
    }

    public List<Long> getWindowPointers() {
        return new ArrayList<>(WINDOW_CACHE.keySet());
    }
//
//    static Window _createWindow(int width, int height, String title, Monitor monitor) {
//        long windowPointer = GLFW.glfwCreateWindow(width, height, title, monitor == null ? 0 : monitor.getPointer(), 0);
//        if (windowPointer == 0) {
//            //TODO THROW
//            return null;
//        }
//        WindowOpenGL32 window = new WindowOpenGL32(windowPointer, width, height, title, monitor);
//        GLFW.glfwSetWindowCloseCallback(windowPointer, (w) -> {
////            SpinyGuiOpenGL32Service.getInstance().destroyWindow(window);
//        });
//        WINDOW_CACHE.put(windowPointer, window);
//        return window;
//    }
//
//    static void _destroyWindow(Window window) {
//        long pointer = window.getPointer();
//        WINDOW_CACHE.remove(pointer, window);
//        GLFW.glfwDestroyWindow(pointer);
//    }
//
//    static boolean _isVisible(Window window) {
//        return GLFW.glfwGetWindowAttrib(window.getPointer(), GLFW.GLFW_VISIBLE) == GLFW.GLFW_TRUE;
//    }
//
//    static void _setVisible(Window window, boolean visible) {
//        GLFW.glfwSetWindowAttrib(window.getPointer(), GLFW.GLFW_VISIBLE, visible ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
//    }
}
