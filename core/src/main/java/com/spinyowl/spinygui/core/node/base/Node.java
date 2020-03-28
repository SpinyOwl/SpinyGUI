package com.spinyowl.spinygui.core.node.base;

import com.spinyowl.spinygui.core.node.intersection.Intersection;
import com.spinyowl.spinygui.core.node.intersection.Intersections;
import java.util.StringJoiner;
import org.joml.Vector2f;
import org.joml.Vector2fc;

/**
 * Base structure of any node.
 * <p>
 * Have three base subclasses that should be used to create any kind of element:
 * <ul>
 * <li>{@link Container}<br> - base for components that could contain other components. </li>
 * <li>{@link EmptyElement}<br> - base for components that could not contain other components. </li>
 * <li>{@link Text}<br> - representation of text node. </li>
 * </ul>
 */
public abstract class Node {

    /**
     * Parent node.
     */
    private Container parent;
    /**
     * Node position. Assigned to node by layout manager.
     */
    private Vector2f position = new Vector2f();
    /**
     * Node size. Assigned to node by layout manager.
     */
    private Vector2f size = new Vector2f();
    /**
     * Node visibility.
     */
    private boolean visible;
    /**
     * Determines whether this node hovered or not (cursor is over this node).
     */
    private boolean hovered;
    /**
     * Determines whether this node focused or not.
     */
    private boolean focused;
    /**
     * Determines whether this node pressed or not (Mouse button is down and on this node).
     */
    private boolean pressed;
    /**
     * Node intersection. During initialization used {@link Intersections#getDefaultIntersection()}.
     * Used to allow detect intersection of point on virtual window surface and node.
     */
    private Intersection intersection = Intersections.getDefaultIntersection();

    /**
     * Returns true if node is hovered.
     *
     * @return true if node is hovered.
     */
    public boolean isHovered() {
        return hovered;
    }

    /**
     * Used to set node hovered or not.
     *
     * @param hovered new hovered state.
     */
    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    /**
     * Returns true if node is focused.
     *
     * @return true if node is focused.
     */
    public boolean isFocused() {
        return focused;
    }

    /**
     * Used to set node focused or not.
     *
     * @param focused new focused state.
     */
    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    /**
     * Returns true if node is pressed.
     *
     * @return true if node is pressed.
     */
    public boolean isPressed() {
        return pressed;
    }

    /**
     * Used to set node pressed or not.
     *
     * @param pressed new pressed state.
     */
    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }

    /**
     * Returns true if node is visible.
     *
     * @return true if node is visible.
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Used to set node visible or not.
     *
     * @param visible new visible state.
     */
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
     * Used to set intersection. If intersection instance is null - intersection will be replaced
     * with default intersection.
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

    /**
     * Returns parent node or null if no parent.
     *
     * @return parent node or null if no parent.
     */
    public Container getParent() {
        return parent;
    }

    /**
     * Used to set parent node.
     *
     * @param parent new parent node.
     */
    public void setParent(Container parent) {
        if (parent == this) {
            return;
        }

        if (this.parent != null) {
            this.parent.removeChild(this);
        }
        this.parent = parent;
        if (parent != null) {
            parent.addChild(this);
        }
    }

    /**
     * Returns node position in coordinates of parent node.
     *
     * @return node position in coordinates of parent node.
     */
    public Vector2fc getPosition() {
        return position;
    }

    /**
     * Used to set node position in coordinates of parent node.
     * <p>
     * Used by layout engine.
     *
     * @param position node position in coordinates of parent node.
     */
    public void setPosition(Vector2fc position) {
        if (position != null) {
            this.position.set(position);
        } else {
            this.position.set(0, 0);
        }
    }

    /**
     * Returns absolute component position.
     *
     * @return position vector.
     */
    public Vector2f getAbsolutePosition() {
        Vector2f screenPos = new Vector2f(this.position);
        for (Node p = this.getParent(); p != null; p = p.getParent()) {
            screenPos.add(p.getPosition());
        }
        return screenPos;
    }

    /**
     * Used to set node position in coordinates of parent node.
     * <p>
     * Used by layout engine.
     *
     * @param x node x position in coordinates of parent node.
     * @param y node y position in coordinates of parent node.
     */
    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    /**
     * Returns node size.
     *
     * @return node size.
     */
    public Vector2fc getSize() {
        return size;
    }

    /**
     * Used to set node size.
     * <p>
     * Used by layout engine.
     *
     * @param size node size.
     */
    public void setSize(Vector2fc size) {
        if (size != null) {
            this.size.set(size);
        } else {
            this.size.set(0, 0);
        }
    }

    /**
     * Used to set node size.
     * <p>
     * Used by layout engine.
     *
     * @param width  node width.
     * @param height node height.
     */
    public void setSize(float width, float height) {
        this.size.set(width, height);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", getClass().getSimpleName() + "[", "]")
            .add("position=" + position)
            .add("size=" + size)
            .add("visible=" + visible)
            .add("hovered=" + hovered)
            .add("focused=" + focused)
            .add("pressed=" + pressed)
            .toString();
    }
}
