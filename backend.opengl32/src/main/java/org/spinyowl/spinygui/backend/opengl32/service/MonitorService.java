package org.spinyowl.spinygui.backend.opengl32.service;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.spinyowl.spinygui.backend.opengl32.MonitorOpenGL32;
import org.spinyowl.spinygui.backend.opengl32.VideoModeOpenGL32;
import org.spinyowl.spinygui.core.api.Monitor;
import org.spinyowl.spinygui.core.api.VideoMode;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class MonitorService {

    private static final Map<Long, Monitor> monitorCache = new ConcurrentHashMap<>();

    static Monitor getPrimaryMonitor() {
        return monitorCache.computeIfAbsent(GLFW.glfwGetPrimaryMonitor(), MonitorService::createMonitor);
    }

    static List<Monitor> getMonitors() {
        PointerBuffer pointerBuffer = GLFW.glfwGetMonitors();

        return null;
    }

    /**
     * Used to create monitor instance from glfw monitor pointer.
     * <p>
     * Unsafe to use outside of callable of {@link SpinyGuiOpenGL32Service#glfwService}.
     *
     * @param monitor monitor pointer.
     * @return instance of {@link Monitor}.
     */
    private static Monitor createMonitor(long monitor) {
        GLFWVidMode.Buffer glfwVidModes = GLFW.glfwGetVideoModes(monitor);
        int capacity = glfwVidModes.capacity();
        for(int i = 0; i < capacity; i++) {
            GLFWVidMode glfwVidMode = glfwVidModes.get(i);
            VideoMode videoMode = createVideoMode(glfwVidMode);
        }
        String monitorName = GLFW.glfwGetMonitorName(monitor);
        int x[] = {0}, y[] = {0};
        GLFW.glfwGetMonitorPos(monitor, x, y);
        float xs[] = {0}, ys[] = {0};
        GLFW.glfwGetMonitorContentScale(monitor, xs, ys);
        int xsize[] = {0}, ysize[] = {0};
        GLFW.glfwGetMonitorPhysicalSize(monitor, xsize, ysize);
        long userPointer = GLFW.glfwGetMonitorUserPointer(monitor);
        if (userPointer == 0) {
            GLFW.glfwSetMonitorUserPointer(monitor, userPointer = 33333);
        }

        return MonitorOpenGL32.createMonitor(monitor, monitorName, userPointer, x[0], y[0], xs[0], ys[0], xsize[0], ysize[0], glfwVidModes);
    }

    private static VideoMode createVideoMode(GLFWVidMode vidMode) {
        return VideoModeOpenGL32.create(vidMode.redBits(), vidMode.greenBits(), vidMode.blueBits(), vidMode.width(), vidMode.height(), vidMode.refreshRate(), vidMode.address());
    }
}
