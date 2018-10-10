package org.liquidengine.legui.core.system.component;

public abstract class Node {

    private String name;
    private Node parent;

    private float x;
    private float y;
    private float width;
    private float height;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
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

    protected abstract void removeChild(Node node);

    protected abstract void addChild(Node node);
}
