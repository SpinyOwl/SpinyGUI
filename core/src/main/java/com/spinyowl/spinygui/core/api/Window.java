package com.spinyowl.spinygui.core.api;

import com.spinyowl.spinygui.core.component.Panel;
import com.spinyowl.spinygui.core.component.base.Container;

public abstract class Window {

    private Container container = new Panel();

    public abstract long getPointer();

    public abstract int getWidth();

    public abstract void setWidth(int width);

    public abstract int getHeight();

    public abstract void setHeight(int height);

    public abstract String getTitle();

    public abstract void setTitle(String title);

    public abstract int getX();

    public abstract void setX(int x);

    public abstract int getY();

    public abstract void setY();

    public abstract void setPosition(int x, int y);

    public abstract void setSize(int width, int height);

    public abstract boolean isVisible();

    public abstract void setVisible(boolean visible);

    public abstract Monitor getMonitor();

    public abstract void setMonitor(Monitor monitor);

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        if (container != null) {
            this.container = container;
        }
    }
}
