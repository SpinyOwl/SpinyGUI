package com.spinyowl.spinygui.core.node;

import com.spinyowl.spinygui.core.node.intersection.Intersection;
import com.spinyowl.spinygui.core.node.intersection.Intersections;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.joml.Vector2f;

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
@Data
@EqualsAndHashCode(exclude = "parent")
@ToString(exclude = "parent")
@NoArgsConstructor
public abstract class Node {
    /**
     * Parent node.
     */
    @Setter(AccessLevel.NONE)
    private Container parent;

    /**
     * Node position. Assigned to node by layout manager.
     */
    @NonNull
    private Vector2f position = new Vector2f();

    /**
     * Node size. Assigned to node by layout manager.
     */
    @NonNull
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
    @NonNull
    private Intersection intersection = Intersections.getDefaultIntersection();

    /**
     * Used to set parent node.
     *
     * @param parent new parent node.
     */
    public void parent(Container parent) {
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
     * Returns absolute component position.
     *
     * @return position vector.
     */
    public Vector2f absolutePosition() {
        Vector2f screenPos = new Vector2f(this.position());
        for (Node p = this.parent(); p != null; p = p.parent()) {
            screenPos.add(p.position());
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
    public void position(float x, float y) {
        this.position.set(x, y);
    }

    /**
     * Used to set node size.
     * <p>
     * Used by layout engine.
     *
     * @param width  node width.
     * @param height node height.
     */
    public void size(float width, float height) {
        this.size.set(width, height);
    }
}
