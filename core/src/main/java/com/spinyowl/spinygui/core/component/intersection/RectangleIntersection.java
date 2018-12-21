package com.spinyowl.spinygui.core.component.intersection;

import com.spinyowl.spinygui.core.component.base.Component;
import org.joml.Vector2f;
import org.joml.Vector2fc;

public class RectangleIntersection implements Intersection {

    /**
     * Intersection rule.
     *
     * @param component component to check intersection.
     * @param x         x coordinates of point to check intersection.
     * @param y         y coordinates of point to check intersection.
     * @return true if component intersected by point.
     */
    @Override
    public boolean intersects(Component component, float x, float y) {
        Vector2fc pos = component.getPosition();
        Vector2fc size = component.getSize();
        return     x >= pos.x()
                && x <= pos.x() + size.x()
                && y >= pos.y()
                && y <= pos.y() + size.y();
    }
}
