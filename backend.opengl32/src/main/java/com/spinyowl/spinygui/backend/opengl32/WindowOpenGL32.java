package com.spinyowl.spinygui.backend.opengl32;

import com.spinyowl.spinygui.backend.opengl32.service.SpinyGuiOpenGL32Service;
import com.spinyowl.spinygui.core.api.Monitor;
import com.spinyowl.spinygui.core.api.Window;

public class WindowOpenGL32 extends Window {
    private long pointer;

    private int width;
    private int height;

    private int x;
    private int y;

    private String title;

    private Monitor monitor;
    private boolean closed = false;

    public WindowOpenGL32(long pointer, int width, int height, String title, Monitor monitor) {
        this.pointer = pointer;
        this.width = width;
        this.height = height;
        this.title = title;
        this.monitor = monitor;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public void setWidth(int width) {
        //TODO CALL UPDATE
        this.width = width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setHeight(int height) {
        //TODO CALL UPDATE
        this.height = height;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        //TODO CALL UPDATE
        this.title = title;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public void setX(int x) {
        //TODO CALL UPDATE
        this.x = x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public void setY() {
        //TODO CALL UPDATE
        this.y = y;
    }

    @Override
    public void setPosition(int x, int y) {
        //TODO CALL UPDATE
        this.x = x;
        this.y = y;
    }

    @Override
    public void setSize(int width, int height) {
        //TODO CALL UPDATE
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean isVisible() {
        return SpinyGuiOpenGL32Service.getInstance().isVisible(this);
    }

    @Override
    public void setVisible(boolean visible) {
        SpinyGuiOpenGL32Service.getInstance().setVisible(this, visible);
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public void close() {
        closed = true;
        SpinyGuiOpenGL32Service.getInstance().destroyWindow(this);
    }

    @Override
    public Monitor getMonitor() {
        return monitor;
    }

    @Override
    public void setMonitor(Monitor monitor) {
        //TODO CALL UPDATE
        this.monitor = monitor;
    }

    @Override
    public long getPointer() {
        return pointer;
    }
}
