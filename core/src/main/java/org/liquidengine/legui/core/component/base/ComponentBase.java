package org.liquidengine.legui.core.component.base;


import java.util.List;
import java.util.Map;

public abstract class ComponentBase {

    private ComponentBase parent;

    private float x;
    private float y;
    private float width;
    private float height;

    public ComponentBase getParent() {
        return parent;
    }

    public void setParent(ComponentBase parent) {
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

    public abstract void removeChild(ComponentBase component);

    public abstract void addChild(ComponentBase component);

    public ComponentBase add(ComponentBase component) {
        this.addChild(component);
        return this;
    }

    public ComponentBase remove(ComponentBase component) {
        this.removeChild(component);
        return this;
    }

    public abstract List<ComponentBase> getChildBaseComponents();

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
