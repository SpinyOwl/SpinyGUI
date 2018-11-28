package com.spinyowl.spinygui.core.api;

import com.spinyowl.spinygui.core.component.Panel;
import com.spinyowl.spinygui.core.component.base.Component;
import com.spinyowl.spinygui.core.component.base.Container;
import com.spinyowl.spinygui.core.event.EventTarget;
import com.spinyowl.spinygui.core.event.WindowCloseEvent;
import com.spinyowl.spinygui.core.event.listener.Listener;
import com.spinyowl.spinygui.core.event.listener.impl.DefaultWindowCloseEventListener;
import com.spinyowl.spinygui.core.system.service.ServiceHolder;
import com.spinyowl.spinygui.core.util.Reference;
import org.joml.Vector2i;

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
public abstract class Window implements EventTarget {
    /**
     * Root panel.
     */
    private Container container = new Panel();
    private volatile boolean closed = false;
    private String title;
    private Vector2i previousCursorPosition;

    private Set<Reference<Listener<WindowCloseEvent>>> windowCloseEventListeners = new CopyOnWriteArraySet<>();

    {
        windowCloseEventListeners.add(Reference.of(new DefaultWindowCloseEventListener()));
    }

    public static Window createWindow(int width, int height, String title) {
        return ServiceHolder.getWindowService().createWindow(width, height, title);
    }

    public static Window createWindow(int width, int height, String title, Monitor monitor) {
        return ServiceHolder.getWindowService().createWindow(width, height, title, monitor);
    }

    public abstract long getPointer();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if(title!=null) {
            this.title = title;
            ServiceHolder.getWindowService().setWindowTitle(this, title);
        }
    }

    public Vector2i getPosition() {
        return ServiceHolder.getWindowService().getWindowPosition(this);
    }

    public void setPosition(Vector2i position) {
        ServiceHolder.getWindowService().setWindowPosition(this, position);
    }

    public void setPosition(int x, int y) {
        setPosition(new Vector2i(x, y));
    }

    public Vector2i getSize() {
        return ServiceHolder.getWindowService().getWindowSize(this);
    }

    public void setSize(Vector2i size) {
        ServiceHolder.getWindowService().setWindowSize(this, size);
    }

    public void setSize(int width, int height) {
        this.setSize(new Vector2i(width, height));
    }

    public boolean isVisible(){
        return ServiceHolder.getWindowService().isWindowVisible(this);
    }

    public void setVisible(boolean visible) {
        ServiceHolder.getWindowService().setWindowVisible(this, visible);
    }

    public boolean isClosed() {
        return closed;
    }

    public void close() {
        closed = ServiceHolder.getWindowService().closeWindow(this);
    }

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

    public abstract Component getFocusOwner();
}
