package com.spinyowl.spinygui.core.node.base;

import java.util.Collections;
import java.util.List;

/**
 * Defines node that can not contain child elements.
 */
public abstract class EmptyElement extends Element {


    /**
     * Used to get child nodes.
     *
     * For empty node returns empty list.
     *
     * @return list of child nodes.
     */
    @Override
    public List<Node> getChildNodes() {
        return Collections.emptyList();
    }


    /**
     * Child operations are not supported for {@link EmptyElement}.
     *
     * @param node node.
     * @throws UnsupportedOperationException because child operations are not supported for {@link EmptyElement}.
     */
    @Override
    public final void removeChild(Node node) {
        throw new UnsupportedOperationException("Child operations are not supported for EmptyNode.");
    }

    /**
     * Child operations are not supported for {@link EmptyElement}.
     *
     * @param node node.
     * @throws UnsupportedOperationException because child operations are not supported for {@link EmptyElement}.
     */
    @Override
    public final void addChild(Node node) {
        throw new UnsupportedOperationException("Child operations are not supported for EmptyNode.");
    }

    /**
     * Returns the number of elements in this node.  If this node contains
     * more than {@code Integer.MAX_VALUE} elements, returns
     * {@code Integer.MAX_VALUE}.
     *
     * @return the number of elements in this node
     */
    @Override
    public int count() {
        return 0;
    }

    /**
     * Returns true if there is no child nodes in this node.
     *
     * @return true if there is no child nodes in this node.
     */
    @Override
    public boolean isEmpty() {
        return true;
    }
}
