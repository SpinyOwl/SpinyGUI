package com.spinyowl.spinygui.backend.opengl32.service;

import com.spinyowl.spinygui.core.system.service.TimeService;
import org.lwjgl.glfw.GLFW;

public class SpinyGuiOpenGL32TimeService implements TimeService {
    private static final SpinyGuiOpenGL32TimeService INSTANCE = new SpinyGuiOpenGL32TimeService();
private SpinyGuiOpenGL32TimeService() {
    }

    public static SpinyGuiOpenGL32TimeService getInstance() {
        return INSTANCE;
    }
@Override
    public double getTime() {
        return GLFW.glfwGetTime();
    }
}
