package com.spinyowl.spinygui.backend.opengl32.service;

import com.spinyowl.spinygui.backend.glfwutil.callback.CallbackKeeper;
import com.spinyowl.spinygui.backend.glfwutil.callback.DefaultCallback;
import com.spinyowl.spinygui.backend.glfwutil.callback.DefaultCallbackKeeper;
import com.spinyowl.spinygui.backend.opengl32.api.WindowOpenGL32;
import com.spinyowl.spinygui.backend.opengl32.service.internal.SpinyGuiOpenGL32Service;
import com.spinyowl.spinygui.core.api.Monitor;
import com.spinyowl.spinygui.core.api.Window;
import com.spinyowl.spinygui.core.system.context.Context;
import com.spinyowl.spinygui.core.system.service.WindowService;
import org.joml.Vector2d;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class SpinyGuiOpenGL32WindowService implements WindowService {

    private static final SpinyGuiOpenGL32WindowService INSTANCE = new SpinyGuiOpenGL32WindowService();
    private static final Map<Long, Window> WINDOW_CACHE = new ConcurrentHashMap<>();
    private static final Map<Window, Context> WINDOW_CONTEXT_MAP = new ConcurrentHashMap<>();

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
        Objects.requireNonNull(title);
        return SpinyGuiOpenGL32Service.getInstance().addTaskAndGet(() -> createWindowGL32(width, height, title, monitor));
    }

    private WindowOpenGL32 createWindowGL32(int width, int height, String title, Monitor monitor) {

        Objects.requireNonNull(title);
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        long share = SpinyGuiOpenGL32Service.getInstance().getHiddenContext();
        long mon = monitor == null ? 0 : monitor.getPointer();
        long windowPointer = GLFW.glfwCreateWindow(width, height, title, mon, share);
        if (windowPointer == 0) {
            throw new RuntimeException("Can't create window with specified parameters");
        }
        CallbackKeeper keeper = new DefaultCallbackKeeper();
        CallbackKeeper.registerCallbacks(windowPointer, keeper);
        addDefaultEventListeners(keeper);

        WindowOpenGL32 window = new WindowOpenGL32(windowPointer, monitor, keeper);

        WINDOW_CACHE.put(windowPointer, window);
        WINDOW_CONTEXT_MAP.put(window, new Context(windowPointer));
        return window;
    }

    private void addDefaultEventListeners(CallbackKeeper keeper) {
        keeper.getChainCharCallback().add(DefaultCallback.createCharCallback());
        keeper.getChainCharModsCallback().add(DefaultCallback.createCharModsCallback());
        keeper.getChainCursorEnterCallback().add(DefaultCallback.createCursorEnterCallback());
        keeper.getChainCursorPosCallback().add(DefaultCallback.createCursorPosCallback());
        keeper.getChainDropCallback().add(DefaultCallback.createDropCallback());
        keeper.getChainFramebufferSizeCallback().add(DefaultCallback.createFramebufferSizeCallback());
        keeper.getChainKeyCallback().add(DefaultCallback.createKeyCallback());
        keeper.getChainMouseButtonCallback().add(DefaultCallback.createMouseButtonCallback());
        keeper.getChainScrollCallback().add(DefaultCallback.createScrollCallback());
        keeper.getChainWindowCloseCallback().add(DefaultCallback.createWindowCloseCallback());
        keeper.getChainWindowContentScaleCallback().add(DefaultCallback.createWindowContentScaleCallback());
        keeper.getChainWindowFocusCallback().add(DefaultCallback.createWindowFocusCallback());
        keeper.getChainWindowIconifyCallback().add(DefaultCallback.createWindowIconifyCallback());
        keeper.getChainWindowMaximizeCallback().add(DefaultCallback.createWindowMaximizeCallback());
        keeper.getChainWindowPosCallback().add(DefaultCallback.createWindowPosCallback());
        keeper.getChainWindowRefreshCallback().add(DefaultCallback.createWindowRefreshCallback());
        keeper.getChainWindowSizeCallback().add(DefaultCallback.createWindowSizeCallback());
    }

    public List<Window> getWindows() {
        return new ArrayList<>(WINDOW_CACHE.values());
    }

    public boolean closeWindow(Window window) {
        Objects.requireNonNull(window);
        return SpinyGuiOpenGL32Service.getInstance().addTaskAndGet(() -> {
            long pointer = window.getPointer();
            if (WINDOW_CACHE.containsKey(pointer)) {
                WINDOW_CACHE.remove(pointer, window);
                WINDOW_CONTEXT_MAP.remove(window);
                GLFW.glfwDestroyWindow(pointer);
            }
            return true;
        });
    }

    public List<Long> getWindowPointers() {
        return new ArrayList<>(WINDOW_CACHE.keySet());
    }

    @Override
    public Window getWindow(long pointer) {
        return WINDOW_CACHE.get(pointer);
    }

    public Context getContext(Window window) {
        return WINDOW_CONTEXT_MAP.get(window);
    }

    @Override
    public Vector2i getWindowSize(Window window) {
        Objects.requireNonNull(window);
        return SpinyGuiOpenGL32Service.getInstance().addTaskAndGet(() -> {
            int[] x = {0}, y = {0};
            GLFW.glfwGetWindowSize(window.getPointer(), x, y);
            return new Vector2i(x[0], y[0]);
        });
    }

    @Override
    public void setWindowSize(Window window, Vector2i size) {
        Objects.requireNonNull(window);
        Objects.requireNonNull(size);
        SpinyGuiOpenGL32Service.getInstance().addTask(
                () -> GLFW.glfwSetWindowSize(window.getPointer(), size.x, size.y)
        );
    }

    @Override
    public Vector2i getWindowPosition(Window window) {
        Objects.requireNonNull(window);
        return SpinyGuiOpenGL32Service.getInstance().addTaskAndGet(() -> {
            int[] x = {0}, y = {0};
            GLFW.glfwGetWindowPos(window.getPointer(), x, y);
            return new Vector2i(x[0], y[0]);
        });
    }

    @Override
    public void setWindowPosition(Window window, Vector2i position) {
        Objects.requireNonNull(window);
        Objects.requireNonNull(position);
        SpinyGuiOpenGL32Service.getInstance().addTask(() ->
                GLFW.glfwSetWindowPos(window.getPointer(), position.x, position.y)
        );
    }

    @Override
    public boolean isWindowVisible(Window window) {
        Objects.requireNonNull(window);
        return SpinyGuiOpenGL32Service.getInstance().addTaskAndGet(() -> GLFW.GLFW_TRUE == GLFW.glfwGetWindowAttrib(window.getPointer(), GLFW.GLFW_VISIBLE));
    }

    @Override
    public void setWindowVisible(Window window, boolean visible) {
        Objects.requireNonNull(window);
        SpinyGuiOpenGL32Service.getInstance().addTask(() -> {
            if (visible) {
                GLFW.glfwShowWindow(window.getPointer());
            } else {
                GLFW.glfwHideWindow(window.getPointer());
            }
        });
    }

    @Override
    public void setWindowTitle(Window window, String title) {
        Objects.requireNonNull(window);
        Objects.requireNonNull(title);
        SpinyGuiOpenGL32Service.getInstance().addTask(() -> GLFW.glfwSetWindowTitle(window.getPointer(), title));
    }

    @Override
    public Vector2d getCursorPosition(Window window) {
        Objects.requireNonNull(window);
        return SpinyGuiOpenGL32Service.getInstance().addTaskAndGet(() -> {
            double[] x = {0}, y = {0};
            GLFW.glfwGetCursorPos(window.getPointer(), x, y);
            return new Vector2d(x[0], y[0]);
        });
    }

    @Override
    public void setCursorPosition(Window window, Vector2d position) {
        Objects.requireNonNull(window);
        Objects.requireNonNull(position);
        SpinyGuiOpenGL32Service.getInstance().addTask(() ->
                GLFW.glfwSetCursorPos(window.getPointer(), position.x, position.y)
        );
    }

}