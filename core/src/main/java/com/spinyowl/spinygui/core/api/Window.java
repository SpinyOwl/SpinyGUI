package com.spinyowl.spinygui.core.api;

import com.spinyowl.spinygui.core.component.Panel;
import com.spinyowl.spinygui.core.component.base.Container;
import com.spinyowl.spinygui.core.event.WindowCloseEvent;
import com.spinyowl.spinygui.core.event.listener.Listener;
import com.spinyowl.spinygui.core.event.listener.impl.DefaultWindowCloseEventListener;
import com.spinyowl.spinygui.core.service.ServiceHandler;
import com.spinyowl.spinygui.core.util.Reference;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;


/**
 * Window class. Represents window in OS.
 * <br>
 * <br>
 * <b>If you need to add custom functionality to winodw class - you need to create proxy for instance created by static method!</b>
 */
public abstract class Window {
    /**
     * Root panel.
     */
    private Container container = new Panel();
    private volatile boolean closed = false;

    private Set<Reference<Listener<WindowCloseEvent>>> windowCloseEventListeners = new CopyOnWriteArraySet<>();

    {
        windowCloseEventListeners.add(Reference.of(new DefaultWindowCloseEventListener()));
    }

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

    public boolean isClosed() {
        return closed;
    }

    public void close() {
        closed = ServiceHandler.getWindowService().closeWindow(this);
    }

    public abstract Monitor getMonitor();

    public abstract void setMonitor(Monitor monitor);

    public static Window createWindow(int width, int height, String title) {
        return ServiceHandler.getWindowService().createWindow(width, height, title);
    }

    public static Window createWindow(int width, int height, String title, Monitor monitor) {
        return ServiceHandler.getWindowService().createWindow(width, height, title, monitor);
    }

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        if (container != null) {
            this.container = container;
        }
    }

    public void addWindowCloseEventListener(Listener<WindowCloseEvent> listener) {
        if (listener != null) {
            windowCloseEventListeners.add(Reference.of(listener));
        }
    }

    public void removeWindowCloseEventListener(Listener<WindowCloseEvent> listener) {
        if (listener != null) {
            windowCloseEventListeners.remove(Reference.of(listener));
        }
    }

    public List<Listener<WindowCloseEvent>> getWindowCloseEventListeners() {
        return windowCloseEventListeners.stream().map(Reference::get).collect(Collectors.toList());
    }
}
