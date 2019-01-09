package com.spinyowl.spinygui.core.component.intersection;

public final class Intersections {

    public static final Intersection RECTANGLE_INTERSECTION = new RectangleIntersection();

    private static Intersection defaultIntersection = RECTANGLE_INTERSECTION;

    private Intersections() {
    }

    public static Intersection getDefaultIntersection() {
        return defaultIntersection;
    }

    public static void setDefaultIntersection(Intersection defaultIntersection) {
        if (defaultIntersection != null) {
            Intersections.defaultIntersection = defaultIntersection;
        }
    }
}
