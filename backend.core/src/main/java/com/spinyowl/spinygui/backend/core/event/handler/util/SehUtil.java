package com.spinyowl.spinygui.backend.core.event.handler.util;

import com.spinyowl.spinygui.core.node.base.Container;
import com.spinyowl.spinygui.core.node.base.Node;

import java.util.ArrayList;
import java.util.List;

public final class SehUtil {

    private SehUtil() {
    }

    /**
     * Used to find target node for provided layer and vector. Target means top node which intersected by provided point(vector).
     *
     * @param container root container to search.
     * @param x         x point coordinates to search.
     * @param y         y point coordinates to search.
     * @return top node from layer intersected by vector.
     */
    public static Node getTargetComponent(Container container, final float x, final float y) {
        Node target = container;
        List<Node> childNodes = container.getChildNodes();
        for (Node child : childNodes) {
            target = recursiveTargetComponentSearch(child, target, x, y);
        }
        return target;
    }

    /**
     * Used to search target node (under point) in node. Target means top node which intersected by provided point(vector).
     *
     * @param node   source node to search target.
     * @param target current target.
     * @param x      x point coordinates to search.
     * @param y      y point coordinates to search.
     * @return the top visible node under point.
     */
    private static Node recursiveTargetComponentSearch(Node node, Node target, final float x, final float y) {
        Node newtarget = target;
        if (isaTarget(node, x, y)) {
            newtarget = node;
            List<Node> childNodes = node.getChildNodes();
            for (Node child : childNodes) {
                newtarget = recursiveTargetComponentSearch(child, newtarget, x, y);
            }
        }
        return newtarget;
    }


    /**
     * Used to search all components (under point) in node.
     *
     * @param container root container to search.
     * @param x         x point coordinates to search.
     * @param y         y point coordinates to search.
     * @return all top visible components in layer under point(vector).
     */
    public static List<Node> getTargetComponentList(Container container, final float x, final float y) {
        List<Node> targetList = new ArrayList<>();
        recursiveTargetComponentListSearch(container, targetList, x, y);
        return targetList;
    }


    /**
     * Used to search all components (under point) in node. New located target node will be added to target list.
     *
     * @param node       source node to search target.
     * @param targetList current target list.
     * @param x          x point coordinates to search.
     * @param y          y point coordinates to search.
     */
    public static void recursiveTargetComponentListSearch(Node node, List<Node> targetList, final float x, final float y) {
        if (isaTarget(node, x, y)) {
            targetList.add(node);
            List<Node> childNodes = node.getChildNodes();
            for (Node child : childNodes) {
                recursiveTargetComponentListSearch(child, targetList, x, y);
            }
        }
    }

    private static boolean isaTarget(Node node, float x, float y) {
        return node.isVisible() && node.getIntersection().intersects(node, x, y);
    }
}
