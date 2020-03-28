package com.spinyowl.spinygui.core.api;

import com.spinyowl.spinygui.core.event.WindowCloseEvent;
import com.spinyowl.spinygui.core.event.listener.EventListener;
import com.spinyowl.spinygui.core.node.base.Container;
import java.util.List;
import java.util.Objects;

/**
 * Layer class is root container for all nodes in layer. Contains root node of layer - layer
 * container, list of stylesheets which should be used to calculate node styles
 */
public class Layer extends Container {

    /**
     * Parent frame.
     */
    private Frame frame;

    /**
     * Determines if  current layer allow to pass events to bottom layer if event wasn't handled by
     * components of this layer.
     */
    private boolean eventPassable = true;

    /**
     * Determines if current layer and all of it components can receive events.
     */
    private boolean eventReceivable = true;

    /**
     * Returns frame to which attached this layer.
     *
     * @return frame to which attached this layer.
     */
    public Frame getFrame() {
        return frame;
    }

    /**
     * Used to attach layer to frame.
     *
     * @param frame frame to attach.
     */
    protected void setFrame(Frame frame) {
        if (frame == this.frame) {
            return;
        }
        if (this.frame != null) {
            this.frame.removeLayer(this);
        }
        this.frame = frame;
        if (frame != null) {
            frame.addLayer(this);
        }
    }

    /**
     * Returns true if layer is event passable.
     *
     * @return true if layer is event passable.
     */
    public boolean isEventPassable() {
        return eventPassable;
    }

    /**
     * Used to enable or disable passing events to bottom layer.
     *
     * @param eventPassable true/false to enable/disable passing events to bottom layer.
     */
    public void setEventPassable(boolean eventPassable) {
        this.eventPassable = eventPassable;
    }

    /**
     * Returns true if layer is event receivable.
     *
     * @return true if layer is event receivable.
     */
    public boolean isEventReceivable() {
        return eventReceivable;
    }

    /**
     * Used to enable or disable receiving events by layer.
     *
     * @param eventReceivable true/false to enable/disable receiving events by layer.
     */
    public void setEventReceivable(boolean eventReceivable) {
        this.eventReceivable = eventReceivable;
    }

    public void addWindowCloseEventListener(EventListener<WindowCloseEvent> listener) {
        Objects.requireNonNull(listener);

        addListener(WindowCloseEvent.class, listener);
    }

    public void removeWindowCloseEventListener(EventListener<WindowCloseEvent> listener) {
        Objects.requireNonNull(listener);

        removeListener(WindowCloseEvent.class, listener);
    }

    public List<EventListener<WindowCloseEvent>> getWindowCloseEventListeners() {
        return getListeners(WindowCloseEvent.class);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Layer layer = (Layer) o;
        return eventPassable == layer.eventPassable &&
            eventReceivable == layer.eventReceivable &&
            Objects.equals(frame, layer.frame);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), frame, eventPassable, eventReceivable);
    }
}