package com.spinyowl.spinygui.backend.opengl32;

import com.google.common.base.Objects;
import com.spinyowl.spinygui.core.api.VideoMode;

public class VideoModeOpenGL32 implements VideoMode {
    private final int redBits;
    private final int greenBits;
    private final int blueBits;
    private final int width;
    private final int height;
    private final int refreshRate;
    private final long address;

    public VideoModeOpenGL32(int redBits, int greenBits, int blueBits, int width, int height, int refreshRate, long address) {
        this.redBits = redBits;
        this.greenBits = greenBits;
        this.blueBits = blueBits;
        this.width = width;
        this.height = height;
        this.refreshRate = refreshRate;
        this.address = address;
    }

    public int getRedBits() {
        return redBits;
    }

    public int getGreenBits() {
        return greenBits;
    }

    public int getBlueBits() {
        return blueBits;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getRefreshRate() {
        return refreshRate;
    }

    public long getAddress() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VideoModeOpenGL32 that = (VideoModeOpenGL32) o;
        return redBits == that.redBits &&
                greenBits == that.greenBits &&
                blueBits == that.blueBits &&
                width == that.width &&
                height == that.height &&
                refreshRate == that.refreshRate &&
                address == that.address;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(redBits, greenBits, blueBits, width, height, refreshRate, address);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("redBits", redBits)
                .add("greenBits", greenBits)
                .add("blueBits", blueBits)
                .add("width", width)
                .add("height", height)
                .add("refreshRate", refreshRate)
                .add("address", address)
                .toString();
    }
}
