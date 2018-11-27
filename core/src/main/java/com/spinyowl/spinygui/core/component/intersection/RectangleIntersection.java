package com.spinyowl.spinygui.core.component.intersection;

import com.spinyowl.spinygui.core.component.base.Component;

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
        return     x >= component.getX()
                && x <= component.getX() + component.getWidth()
                && y >= component.getY()
                && y <= component.getY() + component.getHeight();
    }
}
