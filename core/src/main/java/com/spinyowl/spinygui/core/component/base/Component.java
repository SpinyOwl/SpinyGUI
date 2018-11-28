package com.spinyowl.spinygui.core.component.base;


import com.spinyowl.spinygui.core.component.intersection.Intersection;
import com.spinyowl.spinygui.core.component.intersection.Intersections;
import com.spinyowl.spinygui.core.event.EventTarget;
import org.joml.Vector2f;

import java.util.List;
import java.util.Map;

/**
 * Base structure of any component.
 * <p>
 * Have three base subclasses that should be used to create any kind of element:
 * <ul>
 * <li>{@link Container}<br> - base for components that could contain other components. </li>
 * <li>{@link EmptyComponent}<br> - base for components that could not contain other components. </li>
 * <li>{@link Text}<br> - representation of text node. </li>
 * </ul>
 */
public abstract class Component implements EventTarget {

    /**
     * Parent component.
     */
    private Component parent;

    /**
     * Component position. Mostly assigned to component by layout manager.
     */
    private Vector2f position = new Vector2f();

    /**
     * Component size. Mostly assigned to component by layout manager.
     */
    private Vector2f size;

    /**
     * Component visibility.
     */
    private boolean visible;

    /**
     * Component intersection. During initialization used {@link Intersections#getDefaultIntersection()}.
     * Used to allow detect intersection of point on virtual window surface and component.
     */
    private Intersection intersection = Intersections.getDefaultIntersection();

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Returns intersection instance.
     *
     * @return intersection instance.
     */
    public Intersection getIntersection() {
        return intersection;
    }

    /**
     * Used to set intersection.
     * If intersection instance is null - intersection will be replaced with default intersection.
     *
     * @param intersection intersection to set.
     */
    public void setIntersection(Intersection intersection) {
        if (intersection != null) {
            this.intersection = intersection;
        } else {
            this.intersection = Intersections.getDefaultIntersection();
        }
    }

    public Component getParent() {
        return parent;
    }

    public void setParent(Component parent) {
        if (parent == this) return;
        if (parent == null) throw new NullPointerException("Parent node could not be null.");

        if (this.parent != null) this.parent.removeChild(this);
        this.parent = parent;
        parent.addChild(this);
    }

    public Vector2f getPosition() {
        return position;
    }

    public void setPosition(Vector2f position) {
        if (position != null) {
            this.position = position;
        } else {
            this.position = new Vector2f();
        }
    }

    public Vector2f getSize() {
        return size;
    }

    public void setSize(Vector2f size) {
        if (size != null) {
            this.size = size;
        } else {
            this.size = new Vector2f();
        }
    }

    public abstract void removeChild(Component component);

    public abstract void addChild(Component component);

    /**
     * Add method that calls {@link #addChild(Component)} method and returns {@literal this}.
     *
     * @param component component to add.
     * @return this.
     */
    public final Component add(Component component) {
        this.addChild(component);
        return this;
    }

    /**
     * Remove method that calls {@link #addChild(Component)} method and returns {@literal this}.
     *
     * @param component component to add.
     * @return this.
     */
    public final Component remove(Component component) {
        this.removeChild(component);
        return this;
    }

    public abstract List<Component> getChildComponents();

    /**
     * Returns unmodifiable collection of node attributes.
     *
     * @return unmodifiable collection of node attributes.
     */
    public abstract Map<String, String> getAttributes();

    /**
     * Used to set attribute.
     *
     * @param key   attribute name.
     * @param value attribute value.
     */
    public abstract void setAttribute(String key, String value);

    /**
     * Used to get attribute.
     *
     * @param key attribute name.
     * @return attribute value.
     */
    public abstract String getAttribute(String key);

    /**
     * Used to remove attribute.
     *
     * @param key attribute name.
     */
    public abstract void removeAttribute(String key);
}
