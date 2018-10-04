package org.liquidengine.legui.core.api;

public interface Window {

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
}
