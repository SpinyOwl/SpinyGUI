package com.spinyowl.spinygui.core.node;

import java.util.Collections;
import java.util.List;

/**
 * Defines node that can not contain child elements.
 */
public abstract class EmptyElement extends Element {

    /**
     * Used to get child nodes.
     * <p>
     * For empty node returns empty list.
     *
     * @return list of child nodes.
     */
    @Override
    public List<Node> childNodes() {
        return Collections.emptyList();
    }

    /**
     * Child operations are not supported for {@link EmptyElement}.
     *
     * @param node node.
     * @throws UnsupportedOperationException because child operations are not supported for {@link
     *                                       EmptyElement}.
     */
    @Override
    public final void removeChild(Node node) {
        throw new UnsupportedOperationException(
            "Child operations are not supported for EmptyNode.");
    }

    /**
     * Child operations are not supported for {@link EmptyElement}.
     *
     * @param node node.
     * @throws UnsupportedOperationException because child operations are not supported for {@link
     *                                       EmptyElement}.
     */
    @Override
    public final void addChild(Node node) {
        throw new UnsupportedOperationException(
            "Child operations are not supported for EmptyNode.");
    }

    /**
     * Returns true if an element has any child nodes, otherwise false.
     *
     * @return true if an element has any child nodes, otherwise false.
     */
    @Override
    public boolean hasChildNodes() {
        return false;
    }
}
