package com.spinyowl.spinygui.backend.opengl32;

import com.google.common.base.Objects;
import com.spinyowl.spinygui.core.api.Monitor;
import com.spinyowl.spinygui.core.api.VideoMode;

import java.util.List;

public class MonitorOpenGL32 implements Monitor {

    private final long pointer;
    private final String monitorName;
    private final long userPointer;
    private final int posX;
    private final int posY;
    private final float scaleX;
    private final float scaleY;
    private final int sizeX;
    private final int sizeY;
    private List<VideoMode> videoModes;
    private VideoMode videoMode;

    public MonitorOpenGL32(long pointer, String monitorName, long userPointer,
                           int posX, int posY,
                           float scaleX, float scaleY,
                           int sizeX, int sizeY, List<VideoMode> videoModes) {
        this.pointer = pointer;
        this.monitorName = monitorName;
        this.userPointer = userPointer;
        this.posX = posX;
        this.posY = posY;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.videoModes = videoModes;
    }

    @Override
    public VideoMode getVideoMode() {
        return videoMode;
    }

    @Override
    public void setVideoMode(VideoMode videoMode) {
        //TODO IMPLEMENT VIDEOMODE UPDATE.
        this.videoMode = videoMode;
    }

    @Override
    public List<VideoMode> getAvailableVideoModes() {
        return videoModes;
    }

    public long getPointer() {
        return pointer;
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
        return pointer == that.pointer &&
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
        return Objects.hashCode(pointer, monitorName, userPointer, posX, posY, scaleX, scaleY, sizeX, sizeY);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("pointer", pointer)
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
