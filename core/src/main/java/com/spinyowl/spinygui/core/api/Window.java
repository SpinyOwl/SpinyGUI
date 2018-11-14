package com.spinyowl.spinygui.core.api;

public interface Window {

    long getPointer();

    int getWidth();

    void setWidth(int width);

    int getHeight();

    void setHeight(int height);

    String getTitle();

    void setTitle(String title);

    int getX();

    void setX(int x);

    int getY();

    void setY();

    void setPosition(int x, int y);

    void setSize(int width, int height);

    boolean isVisible();

    void setVisible(boolean visible);

    Monitor getMonitor();

    void setMonitor(Monitor monitor);
}
