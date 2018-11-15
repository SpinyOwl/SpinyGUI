package com.spinyowl.spinygui.backend.opengl32.service;

import com.spinyowl.spinygui.backend.opengl32.MonitorOpenGL32;
import com.spinyowl.spinygui.backend.opengl32.VideoModeOpenGL32;
import com.spinyowl.spinygui.core.api.Monitor;
import com.spinyowl.spinygui.core.api.VideoMode;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

class MonitorService {

    private static final Map<Long, Monitor> MONITOR_CACHE = new ConcurrentHashMap<>();
    private static final Map<Long, VideoMode> VIDEO_MODE_CACHE = new ConcurrentHashMap<>();

    static Monitor getPrimaryMonitor() {
        long primaryMonitor = GLFW.glfwGetPrimaryMonitor();
        if (primaryMonitor == 0) return null;
        return MONITOR_CACHE.computeIfAbsent(primaryMonitor, MonitorService::createMonitor);
    }

    static List<Monitor> getMonitors() {
        List<Monitor> monitors = new ArrayList<>();
        PointerBuffer pointerBuffer = GLFW.glfwGetMonitors();
        if (pointerBuffer != null) {
            int capacity = pointerBuffer.capacity();
            for (int i = 0; i < capacity; i++) {
                long monitor = pointerBuffer.get(i);
                if (monitor != 0) {
                    monitors.add(MONITOR_CACHE.computeIfAbsent(monitor, MonitorService::createMonitor));
                }
            }
            return monitors;
        } else {
            return null;
        }
    }

    /**
     * Used to create monitor instance from glfw monitor pointer.
     * <p>
     * Unsafe to use outside of callable of {@link SpinyGuiOpenGL32Service#thread}.
     *
     * @param monitor monitor pointer.
     * @return instance of {@link Monitor}.
     */
    private static Monitor createMonitor(long monitor) {
        GLFWVidMode.Buffer glfwVidModes = GLFW.glfwGetVideoModes(monitor);
        List<VideoMode> videoModes = glfwVidModes != null ?
                glfwVidModes.stream().map(MonitorService::createVideoMode).collect(Collectors.toList()) :
                null;
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

        return new MonitorOpenGL32(monitor, monitorName, userPointer, x[0], y[0], xs[0], ys[0], xsize[0], ysize[0], videoModes);
    }

    private static VideoMode createVideoMode(GLFWVidMode vidMode) {
        if (!VIDEO_MODE_CACHE.containsKey(vidMode.address())) {
            VideoMode videoMode = new VideoModeOpenGL32(
                    vidMode.redBits(), vidMode.greenBits(), vidMode.blueBits(),
                    vidMode.width(), vidMode.height(),
                    vidMode.refreshRate(), vidMode.address());
            VIDEO_MODE_CACHE.put(vidMode.address(), videoMode);
        }
        return VIDEO_MODE_CACHE.get(vidMode.address());
    }
}
