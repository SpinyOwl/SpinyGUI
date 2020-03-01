package com.spinyowl.spinygui.core.node.base;
import com.spinyowl.spinygui.core.util.Reference;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public abstract class Container extends Element {

    /**
     * Child nodes.
     */
    private List<Node> childNodes = new CopyOnWriteArrayList<>();

    /**
     * Used to remove child node.
     *
     * @param node node to remove.
     */
    @Override
    public void removeChild(Node node) {
        childNodes.remove(node);
    }

    /**
     * Used to add child node.
     *
     * @param node node to add.
     */
    @Override
    public void addChild(Node node) {
        if (node == null || node == this || Reference.contains(childNodes, node)) {
            return;
        }

        Container parent = node.getParent();
        if (parent != null) {
            parent.removeChild(node);
        }

        childNodes.add(node);

        node.setParent(this);
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
        return childNodes.size();
    }

    @Override
    public boolean isEmpty() {
        return childNodes.isEmpty();
    }

    /**
     * Used to get child nodes.
     *
     * @return list of child nodes.
     */
    @Override
    public List<Node> getChildNodes() {
        return childNodes.stream().collect(Collectors.toUnmodifiableList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Container container = (Container) o;
        return Objects.equals(childNodes, container.childNodes);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(childNodes);
    }
}
