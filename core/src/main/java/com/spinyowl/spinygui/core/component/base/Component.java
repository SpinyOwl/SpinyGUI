package com.spinyowl.spinygui.core.component.base;


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
public abstract class Component {

    private Component parent;

    private float x;
    private float y;
    private float width;
    private float height;

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

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
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
