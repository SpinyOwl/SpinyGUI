package com.spinyowl.spinygui.core.style;

import com.spinyowl.spinygui.core.api.Monitor;
import com.spinyowl.spinygui.core.api.Window;
import com.spinyowl.spinygui.core.system.render.Renderer;
import com.spinyowl.spinygui.core.system.service.*;
import org.joml.Vector2i;

import java.util.List;

public class TestServiceProvider implements ServiceHolder.ServiceProvider {
    @Override
    public MonitorService getMonitorService() {
        return new MonitorService() {
            @Override
            public Monitor getPrimaryMonitor() {
                return null;
            }

            @Override
            public List<Monitor> getMonitors() {
                return null;
            }
        };
    }

    @Override
    public WindowService getWindowService() {
        return new WindowService() {
            @Override
            public Window createWindow(int width, int height, String title) {
                return null;
            }

            @Override
            public Window createWindow(int width, int height, String title, Monitor monitor) {
                return null;
            }

            @Override
            public boolean closeWindow(Window window) {
                return false;
            }

            @Override
            public Window getWindow(long pointer) {
                return null;
            }

            @Override
            public Vector2i getWindowSize(Window window) {
                return null;
            }

            @Override
            public void setWindowSize(Window window, Vector2i size) {

            }

            @Override
            public Vector2i getWindowPosition(Window window) {
                return null;
            }

            @Override
            public void setWindowPosition(Window window, Vector2i position) {

            }

            @Override
            public boolean isWindowVisible(Window window) {
                return false;
            }

            @Override
            public void setWindowVisible(Window window, boolean visible) {

            }

            @Override
            public void setWindowTitle(Window window, String title) {

            }

            @Override
            public void getCursorPosition(Window window) {

            }
        };
    }

    @Override
    public ClipboardService getClipboardService() {
        return new ClipboardService() {
            @Override
            public String getClipboardString() {
                return null;
            }

            @Override
            public void setClipboardString(String string) {

            }
        };
    }

    @Override
    public RendererFactoryService getRendererFactoryService() {
        return new RendererFactoryService() {
            @Override
            public <T> Renderer<T> getRenderer(Class<T> elementClass) {
                return null;
            }
        };
    }
}
