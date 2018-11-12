package org.spinyowl.spinygui.backend.opengl32;

import com.google.common.base.Objects;
import org.lwjgl.glfw.GLFWVidMode;
import org.spinyowl.spinygui.core.api.Monitor;
import org.spinyowl.spinygui.core.api.VideoMode;

import java.util.List;

public class MonitorOpenGL32 implements Monitor {

    private final long glfwMonitorPointer;
    private final String monitorName;
    private final long userPointer;
    private final int posX;
    private final int posY;
    private final float scaleX;
    private final float scaleY;
    private final int sizeX;
    private final int sizeY;

    private MonitorOpenGL32(long glfwMonitorPointer, String monitorName, long userPointer,
                            int posX, int posY,
                            float scaleX, float scaleY,
                            int sizeX, int sizeY) {
        this.glfwMonitorPointer = glfwMonitorPointer;
        this.monitorName = monitorName;
        this.userPointer = userPointer;
        this.posX = posX;
        this.posY = posY;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }

    public static Monitor createMonitor(long glfwMonitorPointer,
                                        String monitorName, long userPointer,
                                        int posX, int posY,
                                        float scaleX, float scaleY,
                                        int sizeX, int sizeY,
                                        GLFWVidMode.Buffer glfwVidModes) {
        if (glfwMonitorPointer == 0) return null;
        return new MonitorOpenGL32(glfwMonitorPointer, monitorName, userPointer, posX, posY, scaleX, scaleY, sizeX, sizeY);
    }

    @Override
    public VideoMode getVideoMode() {
        return null;
    }

    @Override
    public void setVideoMode(VideoMode videoMode) {

    }

    @Override
    public List<VideoMode> getAvailableVideoModes() {
        return null;
    }

    public long getGlfwMonitorPointer() {
        return glfwMonitorPointer;
    }

    public String getMonitorName() {
        return monitorName;
    }

    public long getUserPointer() {
        return userPointer;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public float getScaleX() {
        return scaleX;
    }

    public float getScaleY() {
        return scaleY;
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MonitorOpenGL32 that = (MonitorOpenGL32) o;
        return glfwMonitorPointer == that.glfwMonitorPointer &&
                userPointer == that.userPointer &&
                posX == that.posX &&
                posY == that.posY &&
                Float.compare(that.scaleX, scaleX) == 0 &&
                Float.compare(that.scaleY, scaleY) == 0 &&
                sizeX == that.sizeX &&
                sizeY == that.sizeY &&
                Objects.equal(monitorName, that.monitorName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(glfwMonitorPointer, monitorName, userPointer, posX, posY, scaleX, scaleY, sizeX, sizeY);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("glfwMonitorPointer", glfwMonitorPointer)
                .add("monitorName", monitorName)
                .add("userPointer", userPointer)
                .add("posX", posX)
                .add("posY", posY)
                .add("scaleX", scaleX)
                .add("scaleY", scaleY)
                .add("sizeX", sizeX)
                .add("sizeY", sizeY)
                .toString();
    }
}
