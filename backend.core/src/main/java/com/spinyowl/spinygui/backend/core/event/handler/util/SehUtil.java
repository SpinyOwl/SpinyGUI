package com.spinyowl.spinygui.backend.core.event.handler.util;

import com.spinyowl.spinygui.core.component.base.Component;
import com.spinyowl.spinygui.core.component.base.Container;

import java.util.ArrayList;
import java.util.List;

public final class SehUtil {

    private SehUtil() {
    }

    /**
     * Used to find target component for provided layer and vector. Target means top component which intersected by provided point(vector).
     *
     * @param container root container to search.
     * @param x         x point coordinates to search.
     * @param y         y point coordinates to search.
     * @return top component from layer intersected by vector.
     */
    public static Component getTargetComponent(Container container, final float x, final float y) {
        Component target = container;
        List<Component> childComponents = container.getChildComponents();
        for (Component child : childComponents) {
            target = recursiveTargetComponentSearch(child, target, x, y);
        }
        return target;
    }

    /**
     * Used to search target component (under point) in component. Target means top component which intersected by provided point(vector).
     *
     * @param component source component to search target.
     * @param target    current target.
     * @param x         x point coordinates to search.
     * @param y         y point coordinates to search.
     * @return the top visible component under point.
     */
    private static Component recursiveTargetComponentSearch(Component component, Component target, final float x, final float y) {
        Component newtarget = target;
        if (isaTarget(component, x, y)) {
            newtarget = component;
            List<Component> childComponents = component.getChildComponents();
            for (Component child : childComponents) {
                newtarget = recursiveTargetComponentSearch(child, newtarget, x, y);
            }
        }
        return newtarget;
    }


    /**
     * Used to search all components (under point) in component.
     *
     * @param container root container to search.
     * @param x         x point coordinates to search.
     * @param y         y point coordinates to search.
     * @return all top visible components in layer under point(vector).
     */
    public static List<Component> getTargetComponentList(Container container, final float x, final float y) {
        List<Component> targetList = new ArrayList<>();
        recursiveTargetComponentListSearch(container, targetList, x, y);
        return targetList;
    }


    /**
     * Used to search all components (under point) in component. New located target component will be added to target list.
     *
     * @param component  source component to search target.
     * @param targetList current target list.
     * @param x          x point coordinates to search.
     * @param y          y point coordinates to search.
     */
    public static void recursiveTargetComponentListSearch(Component component, List<Component> targetList, final float x, final float y) {
        if (isaTarget(component, x, y)) {
            targetList.add(component);
            List<Component> childComponents = component.getChildComponents();
            for (Component child : childComponents) {
                recursiveTargetComponentListSearch(child, targetList, x, y);
            }
        }
    }

    private static boolean isaTarget(Component component, float x, float y) {
        return component.isVisible() && component.getIntersection().intersects(component, x, y);
    }
}
