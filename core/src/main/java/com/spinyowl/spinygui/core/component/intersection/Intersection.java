package com.spinyowl.spinygui.core.component.intersection;

import com.spinyowl.spinygui.core.component.base.Component;

/**
 * Intersection class specifies intersection rules for component and point.
 * Used to allow detect intersection of point on virtual window surface and component.
 */
public interface Intersection {

    /**
     * Intersection rule.
     *
     * @param component component to check intersection.
     * @param x         x coordinates of point to check intersection.
     * @param y         y coordinates of point to check intersection.
     * @return true if component intersected by point.
     */
    boolean intersects(Component component, float x, float y);
}
