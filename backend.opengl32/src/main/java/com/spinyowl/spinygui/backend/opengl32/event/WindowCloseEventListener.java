package com.spinyowl.spinygui.backend.opengl32.event;

import com.spinyowl.spinygui.backend.core.event.SystemWindowCloseEvent;
import com.spinyowl.spinygui.backend.core.event.processor.SystemEventProcessor;
import com.spinyowl.spinygui.backend.opengl32.service.SpinyGuiOpenGL32WindowService;
import org.lwjgl.glfw.GLFWWindowCloseCallback;

public class WindowCloseEventListener extends GLFWWindowCloseCallback {
    @Override
    public void invoke(long window) {
        SystemEventProcessor.getInstance().pushEvent(new SystemWindowCloseEvent(SpinyGuiOpenGL32WindowService.getInstance().getWindow(window)));
    }
}
