package com.spinyowl.spinygui.backend.opengl32.service;

import com.spinyowl.spinygui.backend.opengl32.service.internal.SpinyGuiOpenGL32Service;
import com.spinyowl.spinygui.core.service.ClipboardService;
import org.lwjgl.glfw.GLFW;

public class SpinyGuiOpenGL32ClipboardService implements ClipboardService {

    private static final SpinyGuiOpenGL32ClipboardService INSTANCE = new SpinyGuiOpenGL32ClipboardService();

    static {
        SpinyGuiOpenGL32Service.getInstance().startService();
    }


    private SpinyGuiOpenGL32ClipboardService() {
    }

    public static SpinyGuiOpenGL32ClipboardService getInstance() {
        return INSTANCE;
    }

    /**
     * Used to get string from clipboard.
     *
     * @return string from clipboard.
     */
    @Override
    public String getClipboardString() {
        return SpinyGuiOpenGL32Service.getInstance().addTaskAndGet(() -> GLFW.glfwGetClipboardString(-1));
    }

    /**
     * Used to set string to clipboard.
     *
     * @param string string to set to clipboard.
     */
    @Override
    public void setClipboardString(String string) {
        SpinyGuiOpenGL32Service.getInstance().addTask(() -> GLFW.glfwSetClipboardString(-1, string));
    }
}
